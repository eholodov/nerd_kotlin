package com.dunice.nerd_kotlin.controller

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/matrix-scheduler")
interface MatrixScheduleController {

    @PostMapping("/send-schedule")
    fun getDataFromRiseUp(examDataDTO: List<ExamDTO>)

    @PostMapping("/generate-reminders")
    fun generateReminders(examDataDTO: List<ExamDTO>)
}