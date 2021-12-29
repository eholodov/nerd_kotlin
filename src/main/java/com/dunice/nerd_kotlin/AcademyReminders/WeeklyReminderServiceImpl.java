package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.AcademyReminderService;
import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.RemaindersRepository;
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

    @Autowired
    SlackServiceImpl slackService;
    @Autowired
    AcademyReminderService academyReminderService;
    @Autowired
     private WeeklyIsSendRepository weeklyIsSendRepository;


    @Override
    public void generateWeeklyReminders(List<Event> events, String department) {

        var ids = academyReminderService.getSlackIds(events);

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

        var date = events.get(0).getDate();
        var weekNumber = date.get(WeekFields.ISO.weekOfYear());
        var year = date.getYear();
        String fullWeekNumberYear = String.valueOf(weekNumber) + String.valueOf(year);
        var weeklyIsSend = weeklyIsSendRepository.findOneByWeekNumber(fullWeekNumberYear);

        for(Map.Entry<String, Map<DayOfWeek, List<Event>>>item:employeeDayEvents.entrySet()) {
            var fullName = item.getKey();
            var name = fullName.split(" ");
            MessageBuilder messageBuilder = new MessageBuilder().greetings(name[0]).nextLine().weeklyEvents().nextLine();

            var map = item.getValue();
            var treeMap = new TreeMap<>(map);

            for (Map.Entry<DayOfWeek, List<Event>> data:treeMap.entrySet()) {
                var dataKey= data.getKey();
                var ii = data.getValue().stream().map(iter -> iter.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy"))).distinct().collect(Collectors.toList());

                messageBuilder.addDayOfWeek(dataKey, ii.iterator().next()).nextLine();

                data.getValue().forEach(dates -> {

                    var fullDate = dates.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy"));

                    var time = dates.getDate().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"));

                    if (dates.getTrainee().equals(fullName)) {
                        messageBuilder.passEvent(dates.getEventType(), dates.getInterviewer(), time).nextLine();
                    } else if (dates.getInterviewer().equals(fullName)){
                        messageBuilder.helpWithEvent(dates.getEventType(), dates.getTrainee(), time).nextLine();
                    } else {
                        messageBuilder.watchEvent(dates.getEventType(), dates.getTrainee(), dates.getInterviewer(), time).nextLine();
                    }
                });
            }

            if (weeklyIsSend.isEmpty()) {
                slackService.postMessage(ids.get(fullName), messageBuilder.build());
            }
        }
        WeeklyIsSendDocument weeklyIsSendDocument = new WeeklyIsSendDocument(fullWeekNumberYear);
        weeklyIsSendRepository.save(weeklyIsSendDocument);
    }
}

