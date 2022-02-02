package com.dunice.nerd_kotlin.AcademyRemindersSchedule;

import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderService;
import com.dunice.nerd_kotlin.common.db.WeeklySentDocument;
import kotlin.jvm.Volatile;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.temporal.WeekFields;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WeeklyReminderScheduleService {

    private final WeeklyReminderService weeklyReminderService;
    private final MongoTemplate mongoTemplate;


    @Volatile
    HashMap<String, List<WeeklyReminderTask>> weeklyScheduler = new HashMap<String, List<WeeklyReminderTask>>();

    public void schedule(WeeklySentDocument weeklySentDocument, String department, Map<String, String> fullNameSlackIdsMap) {


            val nextWeekEvents = weeklyReminderService.generateSchedule(weeklySentDocument.getEvents());

            var timer = new Timer();
            val task = new WeeklyReminderTask(nextWeekEvents, fullNameSlackIdsMap, mongoTemplate, weeklyReminderService, weeklySentDocument.getWeekNumber());



            var weekNumber = weeklySentDocument.getEvents().get(0).getDate().get(WeekFields.ISO.weekOfYear());
            var yearNumber = weeklySentDocument.getEvents().get(0).getDate().getYear();


            Calendar c = Calendar.getInstance();
            c.set(Calendar.WEEK_OF_YEAR, weekNumber);
            c.set(Calendar.YEAR, yearNumber);

            timer.schedule(task, c.getTime());

    }



}
