package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.WeeklySentDocument;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendRepository;
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeeklyReminderServiceImpl implements WeeklyReminderService {

    private final SlackServiceImpl slackService;
    private final WeeklyIsSendRepository weeklyIsSendRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public void sendWeeklyReminders(List<Event> events, String department, Map<String, String> fullNameSlackIdsMap) {

        log.info("-> \n department {}, \n fullNameSlackIdsMap {}, \n events {}",
                department,
                fullNameSlackIdsMap,
                events);

        val date = events.get(0).getDate().plusHours(3);

        val currentWeekNumber = date.get(WeekFields.ISO.weekOfYear());
        val currentWeekEvents = currentWeekEvents(events, currentWeekNumber);

        val numberOfWeekAndYear = String.valueOf(currentWeekNumber) + String.valueOf(date.getYear());
        val currentWeekEventsFromDB = weeklyIsSendRepository.findOneByWeekNumberAndDepartment(numberOfWeekAndYear, department);
        val employeeDayEvents = generateSchedule(currentWeekEvents);

        if (currentWeekEventsFromDB.isEmpty()) {
            generateAndSendWeeklyMessage(employeeDayEvents, fullNameSlackIdsMap);
            WeeklySentDocument weeklyIsSendDocument = new WeeklySentDocument(numberOfWeekAndYear, department, MDC.get("requestId"), currentWeekEvents);
            weeklyIsSendRepository.save(weeklyIsSendDocument);
        } else {

            addSlackIdsForCurrentWeekUsers(fullNameSlackIdsMap, currentWeekEventsFromDB.get());
            val now = OffsetDateTime.now();
            val newCorrectedEvents = correctEvents(currentWeekEvents, now);
            val oldCorrectedEvents = correctEvents(Objects.requireNonNull(currentWeekEventsFromDB.get().getEvents()), now);

            val diffs = generateDiffs(newCorrectedEvents, oldCorrectedEvents);

            if (diffs == null) return;

            val removedEventsSchedule = generateSchedule(diffs.component1());

            val addedEventsSchedule = generateSchedule(diffs.component2());

            val mergedEventsSchedule = mergeRemovedAddedEvents(removedEventsSchedule, addedEventsSchedule);

            val messages = generateDiffMessages(mergedEventsSchedule, fullNameSlackIdsMap);

            log.info("generateDiffMessages {}", messages);

            val currenWeekData = currentWeekEventsFromDB.get();
            currenWeekData.setEvents(newCorrectedEvents);
            weeklyIsSendRepository.save(currenWeekData);

            messages.forEach((elem) -> slackService.postMessage(elem.component1(), elem.component2()));
        }
        log.info("<!");
    }

    public void addSlackIdsForCurrentWeekUsers(Map<String, String> fullNameSlackIdsMap, WeeklySentDocument currentWeek) {
       log.info("->");
        val notFoundedRecipientsEvents = new ArrayList<Event>();

        currentWeek.getEvents().forEach((event) -> event.getRecipients().forEach((recipient) -> {
            if (fullNameSlackIdsMap.get(recipient) == null) {
                notFoundedRecipientsEvents.add(event);
            }
        }));

        log.info("notFoundedRecipientsEvents {} ", notFoundedRecipientsEvents);
        val newFullNameSlackIdsMap = slackService.getSlackIds(notFoundedRecipientsEvents);

        fullNameSlackIdsMap.putAll(newFullNameSlackIdsMap);

        log.info("<! \n fullNameSlackIdsMap {} ", fullNameSlackIdsMap);

    }

    public List<Event> currentWeekEvents(List<Event> events, int currentWeekNumber) {
        log.info("-><! \n events {}, \n currentWeekNumber {}", events, currentWeekNumber);
        return events.stream()
                .filter(event -> event.getDate().get(WeekFields.ISO.weekOfYear()) == currentWeekNumber)
                .collect(Collectors.toList());
    }

    public List<Event> correctEvents (List<Event> events, OffsetDateTime now) {
        log.info("-><! \n events {}, \n now {}", events, now);
        return events.stream()
               .filter(event -> event.getDate().isAfter(now))
               .collect(Collectors.toList());
    }

    private Map<String, Map<DayOfWeek, List<List<Event>>>> mergeRemovedAddedEvents(
            Map<String, Map<DayOfWeek, List<Event>>> removedEventsSchedule,
            Map<String, Map<DayOfWeek, List<Event>>> addedEventsSchedule
    ) {
        log.info("-> \n removedEventsSchedule {} \n addedEventsSchedule {}", removedEventsSchedule, addedEventsSchedule);

        val mergedEventsSchedule = new HashMap<String, Map<DayOfWeek, List<List<Event>>>>();

        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> removedItem: removedEventsSchedule.entrySet()) {
            addUserEvents(mergedEventsSchedule, removedItem, 0);
        }
        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> addedItem: addedEventsSchedule.entrySet()) {
            addUserEvents(mergedEventsSchedule, addedItem, 1);
        }
        log.info("<!");
        return mergedEventsSchedule;
    }

    private void addUserEvents(
            Map<String, Map<DayOfWeek, List<List<Event>>>> mergedEventsSchedule,
            Map.Entry<String, Map<DayOfWeek, List<Event>>> removedItem,
            int index) {

        log.info("-> \n mergedEventsSchedule {} \n removedItem {} \n index {}", mergedEventsSchedule, removedItem, index);

        val key = removedItem.getKey();
        val value = removedItem.getValue();

        if (!mergedEventsSchedule.containsKey(key)) {
            mergedEventsSchedule.put(key, new HashMap<>());
        }

        for (Map.Entry<DayOfWeek, List<Event>> dayOfWeekEvents : value.entrySet()) {
            val nestedKey = dayOfWeekEvents.getKey();
            val nestedValue = dayOfWeekEvents.getValue();

            if (!mergedEventsSchedule.get(key).containsKey(nestedKey)) {
                mergedEventsSchedule.get(key).put(
                        nestedKey,
                        List.of(new ArrayList<>(), new ArrayList<>())
                );
            }

           mergedEventsSchedule.get(key).get(nestedKey).get(index).addAll(nestedValue);
        }
        log.info("<!");
    }

    private Pair<List<Event>, List<Event>> generateDiffs(List<Event> newEvents, List<Event> oldEvents) {

        log.info("-> \n newEvents {} \n oldEvents {}", newEvents, oldEvents);

        if (newEvents.equals(oldEvents)) return null;

        val oldEventsHash = new HashSet<>(oldEvents);
        val newEventsHash = new HashSet<>(newEvents);

        newEvents.forEach((event) -> {
            if (oldEventsHash.contains(event)) {
                oldEventsHash.remove(event);
                newEventsHash.remove(event);
            }
        });

        val removedEvents = List.copyOf(oldEventsHash);
        val addedEvents = List.copyOf(newEventsHash);

        log.info("<!");
        return new Pair<>(removedEvents, addedEvents);
    }

    @Override
    public Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events) {

        log.info("-> \n events {}", events);

        final var employeeDayEvents = new HashMap<String, Map<DayOfWeek, List<Event>>>();

        events.forEach((event) -> event.getRecipients().forEach((recipient) -> {

                if (!employeeDayEvents.containsKey(recipient)) {
                    employeeDayEvents.put(recipient, new HashMap<>());
                }

                final var dayOfWeek = event.getDate().plusHours(3).getDayOfWeek();

                if (!employeeDayEvents.get(recipient).containsKey(dayOfWeek)) {
                    employeeDayEvents.get(recipient).put(dayOfWeek, new ArrayList<>());
                }
                employeeDayEvents.get(recipient).get(dayOfWeek).add(event);
            }));
        log.info("<!");
        return employeeDayEvents;
    }

    @Override
    public void generateAndSendWeeklyMessage(Map<String, Map<DayOfWeek, List<Event>>> employeeDayEvents,
                                             Map<String, String> fullNameSlackIdsMap) {

        log.info("-> \n employeeDayEvents {} \n fullNameSlackIdsMap {}", employeeDayEvents, fullNameSlackIdsMap);

        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> item: employeeDayEvents.entrySet()) {

            final var fullName = item.getKey();
            final var name = fullName.split(" ");
            MessageBuilder messageBuilder = new MessageBuilder().greetings(name[0]).nextLine().weeklyEvents().nextLine();

            final var treeMap = new TreeMap<>(item.getValue());

            for (Map.Entry<DayOfWeek, List<Event>> data: treeMap.entrySet()) {
                final var dataKey= data.getKey();
                final var ii = data.getValue().stream().map(iter -> iter.getDate().plusHours(3).format(DateTimeFormatter.ofPattern("dd.MM.yy"))).distinct().collect(Collectors.toList());

                messageBuilder.addDayOfWeek(dataKey, ii.iterator().next()).nextLine();

                data.getValue().forEach(dates -> {
                    final var time = dates.getDate().plusHours(3).format(dateTimeFormatter);

                    if (dates.getTrainee().equals(fullName)) {
                        messageBuilder.passWeeklyEvent(dates.getEventType(), dates.getInterviewer(), time).nextLine();
                    } else if (dates.getInterviewer().equals(fullName)){
                        messageBuilder.conductWeeklyEvent(dates.getEventType(), dates.getTrainee(), time).nextLine();
                    } else {
                        messageBuilder.watchWeeklyEvent(dates.getEventType(), dates.getTrainee(), dates.getInterviewer(), time).nextLine();
                    }
                });
            }
                slackService.postMessage(fullNameSlackIdsMap.get(fullName), messageBuilder.build());
        }
        log.info("<!");
    }


    private ArrayList<Pair<String, String>> generateDiffMessages(Map<String, Map<DayOfWeek, List<List<Event>>>> employeeDayEvents,
                                                                 Map<String, String> fullNameSlackIdsMap) {

        log.info("-> \n employeeDayEvents {} \n fullNameSlackIdsMap {}", employeeDayEvents, fullNameSlackIdsMap);

        val messages = new ArrayList<Pair<String, String>>();
        for (Map.Entry<String, Map<DayOfWeek,  List<List<Event>>>> item : employeeDayEvents.entrySet()) {
            var messageBuilder = new MessageBuilder().hey().nextLine();

            val fullName = item.getKey();

            messageBuilder
                    .diffMessage()
                    .doubleNextLine();

            generateMessageForEmployee(item, messageBuilder, fullName, 0);

            messageBuilder
                    .doubleNextLine();
            generateMessageForEmployee(item, messageBuilder, fullName, 1);

            messages.add(new Pair<>(fullNameSlackIdsMap.get(fullName), messageBuilder.build()));
        }
        log.info("<!");
        return messages;
    }

    private void generateMessageForEmployee(
            Map.Entry<String, Map<DayOfWeek, List<List<Event>>>> item,
            MessageBuilder messageBuilder,
            String fullName,
            int index
    ) {

        log.info("-> \n index {} \n fullName {} \n item {} \n messageBuilder {}", index, fullName, index, messageBuilder);

        final var entrySet = new TreeMap<>(item.getValue()).entrySet();

        var isDiffTypeSet = false;

        for (Map.Entry<DayOfWeek,  List<List<Event>>> data : entrySet) {
            final var dataKey = data.getKey();
            final var ii = data.getValue().get(index).stream().map(iter -> iter.getDate().plusHours(3).format(DateTimeFormatter.ofPattern("dd.MM.yy"))).distinct().collect(Collectors.toList());

            if (ii.isEmpty()) {
                continue;
            }

            if (!isDiffTypeSet) {
                switch (index) {
                    case 0:
                        messageBuilder.diffRemovedEvents().nextLine();
                        break;
                    case 1:
                        messageBuilder.diffAddedEvents().nextLine();
                        break;
                    default:
                        throw new RuntimeException("index not valid!");
                }

                isDiffTypeSet = true;
            }
            messageBuilder.addDayOfWeek(dataKey, ii.iterator().next()).nextLine();

            data.getValue().get(index).forEach(dates -> generateDayMessage(dates, fullName, messageBuilder));
        }
        log.info("<!");
    }

    private void generateDayMessage(Event dates, String fullName, MessageBuilder messageBuilder) {

        log.info("-> \n fullName {} \n dates {} \n messageBuilder {}", fullName, dates, messageBuilder);

        final var time = dates.getDate().plusHours(3).format(dateTimeFormatter);

        if (dates.getTrainee().equals(fullName)) {
            messageBuilder.passWeeklyEvent(dates.getEventType(), dates.getInterviewer(), time).nextLine();
        } else if (dates.getInterviewer().equals(fullName)){
            messageBuilder.conductWeeklyEvent(dates.getEventType(), dates.getTrainee(), time).nextLine();
        } else {
            messageBuilder.watchWeeklyEvent(dates.getEventType(), dates.getTrainee(), dates.getInterviewer(), time).nextLine();
        }
        log.info("<!");
    }
}

