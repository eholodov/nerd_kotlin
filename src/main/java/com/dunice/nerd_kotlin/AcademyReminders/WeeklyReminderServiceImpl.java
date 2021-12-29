package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendDocument;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendRepository;
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklyReminderServiceImpl implements WeeklyReminderService {
    private final SlackServiceImpl slackService;
    private final WeeklyIsSendRepository weeklyIsSendRepository;

    @Autowired
    public WeeklyReminderServiceImpl(SlackServiceImpl slackService,
                                     WeeklyIsSendRepository weeklyIsSendRepository) {
        this.slackService = slackService;
        this.weeklyIsSendRepository = weeklyIsSendRepository;
    }

    @Override
    public void generateWeeklyReminders(List<Event> events, String department) {

        final var ids = slackService.getSlackIds(events);
        final var date = events.get(0).getDate();
        final var fullWeekNumberYear = String.valueOf(date.get(WeekFields.ISO.weekOfYear())) + String.valueOf(date.getYear());
        final var weeklyIsSend = weeklyIsSendRepository.findOneByWeekNumber(fullWeekNumberYear);

        if (weeklyIsSend.isEmpty()) {
            final var employeeDayEvents = generateSchedule(events);
            generateAndSendMessage(employeeDayEvents, ids);
            WeeklyIsSendDocument weeklyIsSendDocument = new WeeklyIsSendDocument(fullWeekNumberYear);
            weeklyIsSendRepository.save(weeklyIsSendDocument);
        }
    }

    @Override
    public Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events) {
        final var employeeEvents = new HashMap<String, List<Event>>();

        events.forEach((event) -> {
            event.getRecipients().forEach((recipient) -> {

                if (!employeeEvents.containsKey(recipient)) {
                    employeeEvents.put(recipient, new ArrayList<Event>());
                }
                employeeEvents.get(recipient).add(event);
            });
        });

        final var employeeDayEvents = new HashMap<String, Map<DayOfWeek, List<Event>>>();

        for (Map.Entry<String, List<Event>> entry : employeeEvents.entrySet()) {

            if (!employeeDayEvents.containsKey(entry.getKey())) {
                employeeDayEvents.put(entry.getKey(), new HashMap<>());
            }
            entry.getValue().forEach((event) -> {
                final var dayOfWeek = event.getDate().getDayOfWeek();
                if (!employeeDayEvents.get(entry.getKey()).containsKey(dayOfWeek)) {
                    employeeDayEvents.get(entry.getKey()).put(dayOfWeek,  new ArrayList<>());
                }
                employeeDayEvents.get(entry.getKey()).get(dayOfWeek).add(event);
            });
        }
        return employeeDayEvents;
    }

    @Override
    public void generateAndSendMessage(Map<String, Map<DayOfWeek, List<Event>>> employeeDayEvents, Map<String, String> ids) {

        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> item: employeeDayEvents.entrySet()) {

            final var fullName = item.getKey();
            final var name = fullName.split(" ");
            MessageBuilder messageBuilder = new MessageBuilder().greetings(name[0]).nextLine().weeklyEvents().nextLine();

            final var treeMap = new TreeMap<>(item.getValue());

            for (Map.Entry<DayOfWeek, List<Event>> data: treeMap.entrySet()) {
                final var dataKey= data.getKey();
                final var ii = data.getValue().stream().map(iter -> iter.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy"))).distinct().collect(Collectors.toList());

                messageBuilder.addDayOfWeek(dataKey, ii.iterator().next()).nextLine();

                data.getValue().forEach(dates -> {
                    final var time = dates.getDate().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"));

                    if (dates.getTrainee().equals(fullName)) {
                        messageBuilder.passWeeklyEvent(dates.getEventType(), dates.getInterviewer(), time).nextLine();
                    } else if (dates.getInterviewer().equals(fullName)){
                        messageBuilder.conductWeeklyEvent(dates.getEventType(), dates.getTrainee(), time).nextLine();
                    } else {
                        messageBuilder.watchWeeklyEvent(dates.getEventType(), dates.getTrainee(), dates.getInterviewer(), time).nextLine();
                    }
                });
            }
                slackService.postMessage(ids.get(fullName), messageBuilder.build());
        }
    }
}

