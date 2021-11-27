package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.academyReminder.dto.AddRemindersDto
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/academy-reminders")
class AcademyReminderControllerImpl(
    private val academyReminderService: AcademyReminderService,
    private val academySchedulerServiceImpl: AcademySchedulerServiceImpl
)
{

    @PostMapping
    fun addReminders(
        @RequestBody dto: AddRemindersDto
    ) {

        academyReminderService.addReminders(dto.data, dto.department)
    }

    @GetMapping
    fun getReminders(): MutableMap<String, List<Instant>> {
        return academySchedulerServiceImpl.getActiveReminders()
    }

    @PostMapping("/refresh-reminders")
    fun refreshAllReminders(): MutableMap<String, List<Instant>> {
        academySchedulerServiceImpl.refreshAllReminders()

        return academySchedulerServiceImpl.getActiveReminders()
    }
}