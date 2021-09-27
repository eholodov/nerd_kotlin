package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.RemaindersService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reminders")
class ReminderControllerImpl(val remaindersService: RemaindersService) : ReminderController {

    override fun refreshCrons() {
        remaindersService.refreshCrons()
    }

    override fun startCrons() {
        remaindersService.startCrons()
    }

    override fun getCurrentCrons() {
        remaindersService.getCurrentCrons()
    }
}