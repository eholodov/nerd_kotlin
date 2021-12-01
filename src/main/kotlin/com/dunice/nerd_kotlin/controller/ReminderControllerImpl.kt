package com.dunice.nerd_kotlin.controller

import com.dunice.nerd_kotlin.services.RemaindersService
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