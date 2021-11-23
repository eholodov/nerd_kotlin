package com.dunice.nerd_kotlin.reminder

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/matrix-reminder")
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