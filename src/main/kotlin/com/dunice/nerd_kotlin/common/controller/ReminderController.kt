package com.dunice.nerd_kotlin.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/reminders")
interface ReminderController {

    @PostMapping("refresh_crons")
    fun refreshCrons()

    @PostMapping("start_crons")
    fun startCrons()

    @GetMapping("get_crons")
    fun getCurrentCrons()

}