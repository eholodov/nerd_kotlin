package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.errors.CustomException;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public interface WeeklyReminderService {

    public void generateWeeklyReminders(List<Event> events, String department) throws CustomException;

    public Map<String, Map<DayOfWeek, List<Event>>> generateSchedule(List<Event> events);

//    public void generateAndSendMessage(Map<String, Map<DayOfWeek, List<Event>>> schedule);
    public void generateAndSendMessage(Map<String, Map<DayOfWeek, List<Event>>> schedule, Map<String, String> ids);

}
