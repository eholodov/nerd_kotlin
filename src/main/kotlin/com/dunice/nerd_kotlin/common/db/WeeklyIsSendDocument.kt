package com.dunice.nerd_kotlin.common.db

import com.dunice.nerd_kotlin.AcademyReminders.types.EventDTO
import com.dunice.nerd_kotlin.academyReminder.types.Event
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.DayOfWeek
import java.util.*

@Document(collection = "weeklySendDocument")
class WeeklyIsSendDocument (

    var weekNumber : String,
    var eventSchedule : Map<String, Map<DayOfWeek, List<EventDTO>>>
        ){
    @Id
    lateinit var id: String
}