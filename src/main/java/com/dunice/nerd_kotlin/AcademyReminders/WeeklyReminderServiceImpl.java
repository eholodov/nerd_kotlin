package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.AcademyReminders.types.EventDTO;
import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendDocument;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendRepository;
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl;
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

    public WeeklyReminderServiceImpl(SlackServiceImpl slackService,
                                     WeeklyIsSendRepository weeklyIsSendRepository) {
        this.slackService = slackService;
        this.weeklyIsSendRepository = weeklyIsSendRepository;
    }


    @Override
    public void generateWeeklyReminders(List<Event> events, String department) {

//        final var ids = slackService.getSlackIds(events);
        final var ids = new HashMap<String, String>();

        final var date = events.get(0).getDate();
        final var fullWeekNumberYear = String.valueOf(date.get(WeekFields.ISO.weekOfYear())) + String.valueOf(date.getYear());


        final var weeklyIsSend = weeklyIsSendRepository.findOneByWeekNumber(fullWeekNumberYear);
        final var employeeDayEvents = generateSchedule(events);


        System.out.println("💀before if💀");
        if (weeklyIsSend.isEmpty()) {

//            generateAndSendMessage(employeeDayEvents, ids);
            WeeklyIsSendDocument weeklyIsSendDocument = new WeeklyIsSendDocument(fullWeekNumberYear, employeeDayEvents);
            weeklyIsSendRepository.insert(weeklyIsSendDocument);
        } else {
            var oldSchedule = weeklyIsSend.get().getEventSchedule();

            var stringEmployeeDayEvents = employeeDayEvents.toString();
            var stringSchedule = oldSchedule.toString();

            if (stringSchedule.equals(stringEmployeeDayEvents)) {
                System.out.println("💀equal💀");
            } else {
                System.out.println("💀NOT equal💀");
            }

            System.out.println("💀in else💀");
        }
    }

//    @Override
//    public Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events) {
//
//        final var employeeDayEvents = new HashMap<String, Map<DayOfWeek, List<Event>>>();
//
//        events.forEach((event) -> {
//            event.getRecipients().forEach((recipient) -> {
//
//                if (!employeeDayEvents.containsKey(recipient)) {
//                    employeeDayEvents.put(recipient, new HashMap<>());
//                }
//                final var dayOfWeek = event.getDate().getDayOfWeek();
//
//                if (!employeeDayEvents.get(recipient).containsKey(dayOfWeek)) {
//                    employeeDayEvents.get(recipient).put(dayOfWeek, new ArrayList<>());
//                }
//                employeeDayEvents.get(recipient).get(dayOfWeek).add(event);
//            });
//        });
//        return employeeDayEvents;
//    }

    @Override
    public Map<String, Map<DayOfWeek, List<EventDTO>>> generateSchedule(List<Event> events) {

        List<EventDTO> testEvents = events.stream().map(EventDTO::toEventDTO).collect(Collectors.toList());

        final var employeeDayEvents = new HashMap<String, Map<DayOfWeek, List<EventDTO>>>();

        testEvents.forEach((event) -> {
            event.getRecipients().forEach((recipient) -> {

                if (!employeeDayEvents.containsKey(recipient)) {
                    employeeDayEvents.put(recipient, new HashMap<>());
                }
                final var dayOfWeek = event.getDayOfWeek();

                if (!employeeDayEvents.get(recipient).containsKey(dayOfWeek)) {
                    employeeDayEvents.get(recipient).put(dayOfWeek, new ArrayList<>());
                }
                employeeDayEvents.get(recipient).get(dayOfWeek).add(event);
            });
        });
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
//            slackService.postMessage(ids.get(fullName), messageBuilder.build());
            System.out.println(messageBuilder.build());
        }
    }
}

