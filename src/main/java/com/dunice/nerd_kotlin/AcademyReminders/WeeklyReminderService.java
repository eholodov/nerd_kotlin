package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.types.Event;

import java.util.List;
import java.util.Map;

public interface WeeklyReminderService {

    public void generateWeeklyReminders(List<Event> events, String department);

}
