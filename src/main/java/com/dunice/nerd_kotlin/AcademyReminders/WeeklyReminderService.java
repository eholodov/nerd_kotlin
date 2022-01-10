package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.types.Event;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public interface WeeklyReminderService {

    void sendWeeklyReminders(List<Event> events, String department, Map<String, String> fullNameSlackIdsMap);

    Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events);

    void generateAndSendWeeklyMessage(Map<String, Map<DayOfWeek, List<Event>>> schedule, Map<String, String> ids);

}
