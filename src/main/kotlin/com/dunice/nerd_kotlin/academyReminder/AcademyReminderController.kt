package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.academyReminder.dto.AddRemindersDto
import com.dunice.nerd_kotlin.academyReminder.types.Event
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/academy-reminders")
interface AcademyReminderController {

    @PostMapping
    fun addReminders(@RequestBody dto: AddRemindersDto
    ) : MutableList<Event>
}
