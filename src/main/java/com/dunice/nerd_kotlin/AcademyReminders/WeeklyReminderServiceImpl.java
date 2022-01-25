package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.WeeklySentDocument;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendRepository;
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl;
import kotlin.Pair;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeeklyReminderServiceImpl implements WeeklyReminderService {

    private final SlackServiceImpl slackService;
    private final WeeklyIsSendRepository weeklyIsSendRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static Logger logger = LoggerFactory.getLogger(WeeklyReminderServiceImpl.class.getName());

    @Autowired
    public WeeklyReminderServiceImpl(SlackServiceImpl slackService,
                                     WeeklyIsSendRepository weeklyIsSendRepository) {
        this.slackService = slackService;
        this.weeklyIsSendRepository = weeklyIsSendRepository;
    }

    public void sendWeeklyReminders(List<Event> events, String department, Map<String, String> fullNameSlackIdsMap) {

        logger.info("-> method sendWeeklyReminders in class {},\n department {}, \n fullNameSlackIdsMap {}, \n events {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                department,
                fullNameSlackIdsMap,
                events);

        final var date = events.get(0).getDate().plusHours(3);

        val currentWeekNumber = date.get(WeekFields.ISO.weekOfYear());
        val currentWeekEvents = currentWeekEvents(events, currentWeekNumber);

        final var numberOfWeekAndYear = String.valueOf(currentWeekNumber) + String.valueOf(date.getYear());
        final var currentWeek = weeklyIsSendRepository.findOneByWeekNumberAndDepartment(numberOfWeekAndYear, department);
        final var employeeDayEvents = generateSchedule(currentWeekEvents);

        if (currentWeek.isEmpty()) {
            generateAndSendWeeklyMessage(employeeDayEvents, fullNameSlackIdsMap);
            WeeklySentDocument weeklyIsSendDocument = new WeeklySentDocument(numberOfWeekAndYear, department, currentWeekEvents);
            weeklyIsSendRepository.save(weeklyIsSendDocument);
        } else {

            addSlackIdsForCurrentWeekUsers(fullNameSlackIdsMap, currentWeek.get());
            val now = OffsetDateTime.now();
            val newCorrectedEvents = correctEvents(currentWeekEvents, now);
            val oldCorrectedEvents = correctEvents(Objects.requireNonNull(currentWeek.get().getEvents()), now);

            val diffs = generateDiffs(newCorrectedEvents, oldCorrectedEvents);

            if (diffs == null) return;

            val removedEventsSchedule = generateSchedule(diffs.component1());

            val addedEventsSchedule = generateSchedule(diffs.component2());

            val mergedEventsSchedule = mergeRemovedAddedEvents(removedEventsSchedule, addedEventsSchedule);

            val messages = generateDiffMessages(mergedEventsSchedule, fullNameSlackIdsMap);

            val currenWeekData = currentWeek.get();
            currenWeekData.setEvents(newCorrectedEvents);
            weeklyIsSendRepository.save(currenWeekData);

            messages.forEach((elem) -> slackService.postMessage(elem.component1(), elem.component2()));
        }
        logger.info("<! method sendWeeklyReminders in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
    }

    public void addSlackIdsForCurrentWeekUsers(Map<String, String> fullNameSlackIdsMap, WeeklySentDocument currentWeek) {
        logger.info("<! method addSlackIdsForCurrentWeekUsers in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
        val notFoundedRecipientsEvents = new ArrayList<Event>();

        currentWeek.getEvents().forEach((event) -> event.getRecipients().forEach((recipient) -> {
            if (fullNameSlackIdsMap.get(recipient) == null) {
                notFoundedRecipientsEvents.add(event);
            }
        }));

        logger.info("<! method addSlackIdsForCurrentWeekUsers notFoundedRecipientsEvents {} ", notFoundedRecipientsEvents);
        val newFullNameSlackIdsMap = slackService.getSlackIds(notFoundedRecipientsEvents);

        fullNameSlackIdsMap.putAll(newFullNameSlackIdsMap);

        logger.info("<! method addSlackIdsForCurrentWeekUsers new fullNameSlackIdsMap {} ", fullNameSlackIdsMap);

    }

    public List<Event> currentWeekEvents(List<Event> events, int currentWeekNumber) {
        return events.stream()
                .filter(event -> event.getDate().get(WeekFields.ISO.weekOfYear()) == currentWeekNumber)
                .collect(Collectors.toList());
    }

    public List<Event> correctEvents (List<Event> events, OffsetDateTime now) {

       return events.stream()
               .filter(event -> event.getDate().isAfter(now))
               .collect(Collectors.toList());
    }

    private Map<String, Map<DayOfWeek, List<List<Event>>>> mergeRemovedAddedEvents(
            Map<String, Map<DayOfWeek, List<Event>>> removedEventsSchedule,
            Map<String, Map<DayOfWeek, List<Event>>> addedEventsSchedule
    ) {
        logger.info("-> method mergeRemovedAddedEvents in class {}, \n removedEventsSchedule {}, \n addedEventsSchedule {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                removedEventsSchedule,
                addedEventsSchedule);

        val mergedEventsSchedule = new HashMap<String, Map<DayOfWeek, List<List<Event>>>>();

        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> removedItem: removedEventsSchedule.entrySet()) {
            addUserEvents(mergedEventsSchedule, removedItem, 0);
        }
        for(Map.Entry<String, Map<DayOfWeek, List<Event>>> addedItem: addedEventsSchedule.entrySet()) {
            addUserEvents(mergedEventsSchedule, addedItem, 1);
        }
        logger.info("<! method mergeRemovedAddedEvents in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
        return mergedEventsSchedule;
    }

    private void addUserEvents(
            Map<String, Map<DayOfWeek,
            List<List<Event>>>> mergedEventsSchedule,
            Map.Entry<String, Map<DayOfWeek, List<Event>>> removedItem,
            int index) {

        logger.info("-> method addUserEvents in class {}, \n mergedEventsSchedule {}, \n removedItem {}, \n index {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                mergedEventsSchedule,
                removedItem,
                index);

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
        logger.info("<! method addUserEvents in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
    }

    private Pair<List<Event>, List<Event>> generateDiffs(List<Event> newEvents, List<Event> oldEvents) {

        logger.info("-> method generateDiffs in class {}, \n newEvents {}, \n oldEvents {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                newEvents,
                oldEvents);

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

        logger.info("<! method generateDiffs in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
        return new Pair<>(removedEvents, addedEvents);
    }

    @Override
    public Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events) {

        logger.info("-> method generateSchedule in class {}, \n events {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                events);

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
        logger.info("<! method generateSchedule in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
        return employeeDayEvents;
    }

    @Override
    public void generateAndSendWeeklyMessage(Map<String, Map<DayOfWeek, List<Event>>> employeeDayEvents,
                                             Map<String, String> fullNameSlackIdsMap) {

        logger.info("-> method generateAndSendWeeklyMessage in class {}, \n employeeDayEvents {}, \n fullNameSlackIdsMap {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                employeeDayEvents,
                fullNameSlackIdsMap);

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
        logger.info("<! method generateAndSendWeeklyMessage in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
    }


    private ArrayList<Pair<String, String>> generateDiffMessages(Map<String, Map<DayOfWeek, List<List<Event>>>> employeeDayEvents,
                                                                 Map<String, String> fullNameSlackIdsMap) {

        logger.info("-> method generateDiffMessages in class {}, \n employeeDayEvents {}, \n fullNameSlackIdsMap {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                employeeDayEvents,
                fullNameSlackIdsMap);

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
        logger.info("<! method generateDiffMessages in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
        return messages;
    }

    private void generateMessageForEmployee(
            Map.Entry<String, Map<DayOfWeek, List<List<Event>>>> item,
            MessageBuilder messageBuilder,
            String fullName,
            int index
    ) {

        logger.info("-> method generateMessageForEmployee in class {}",
                WeeklyReminderServiceImpl.class.getSimpleName());

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
        logger.info("<! method generateMessageForEmployee in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
    }

    private void generateDayMessage(Event dates, String fullName, MessageBuilder messageBuilder) {

        logger.info("-> method generateDayMessage in class {}, \n dates {}, \n fullName {}, \n messageBuilder {}",
                WeeklyReminderServiceImpl.class.getSimpleName(),
                dates,
                fullName,
                messageBuilder);

        final var time = dates.getDate().plusHours(3).format(dateTimeFormatter);

        if (dates.getTrainee().equals(fullName)) {
            messageBuilder.passWeeklyEvent(dates.getEventType(), dates.getInterviewer(), time).nextLine();
        } else if (dates.getInterviewer().equals(fullName)){
            messageBuilder.conductWeeklyEvent(dates.getEventType(), dates.getTrainee(), time).nextLine();
        } else {
            messageBuilder.watchWeeklyEvent(dates.getEventType(), dates.getTrainee(), dates.getInterviewer(), time).nextLine();
        }
        logger.info("<! method generateDayMessage in class {}", WeeklyReminderServiceImpl.class.getSimpleName());
    }
}

