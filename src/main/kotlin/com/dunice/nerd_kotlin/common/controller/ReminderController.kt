package com.dunice.nerd_kotlin.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/matrix-reminder")
interface ReminderController {

    @PostMapping("/refresh-cron")
    fun refreshCrons()

    @PostMapping("/handle-cron")
    fun startCrons()

    @GetMapping
    fun getCurrentCrons()

}