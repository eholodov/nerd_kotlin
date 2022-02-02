package com.dunice.nerd_kotlin.AcademyRemindersSchedule;

import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderService;
import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderServiceImpl;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.WeeklyIsSendRepository;
import com.dunice.nerd_kotlin.common.db.WeeklySentDocument;
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
public class WeeklyReminderTask extends TimerTask {

    private final Map<String, Map<DayOfWeek, List<Event>>> schedule;
    private final Map<String, String> fullNameSlackIdsMap;
    private final MongoTemplate mongoTemplate;
    private final WeeklyReminderService weeklyReminderService;
    private final String numbers;

    @Override
    public void run() {
        weeklyReminderService.generateAndSendWeeklyMessage(schedule, fullNameSlackIdsMap);

        Query query = new Query();
        query.addCriteria(Criteria.where("weekNumber").is(numbers));
        Update update = new Update();
        update.set("isSent", true);
        mongoTemplate.upsert(query, update, WeeklySentDocument.class);
    }
}
