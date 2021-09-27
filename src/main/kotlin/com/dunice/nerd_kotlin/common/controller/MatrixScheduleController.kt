package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/matrix-schedule")
interface MatrixScheduleController {

    @PostMapping("/send-schedule")
    public fun getDataFromRiseUp(examDataDTO: List<ExamDTO>)
}