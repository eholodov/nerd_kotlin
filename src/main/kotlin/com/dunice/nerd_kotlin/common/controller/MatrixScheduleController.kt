package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/matrix-scheduler")
interface MatrixScheduleController {

    @PostMapping
    public fun getDataFromRiseUp(examDataDTO: List<ExamDTO>)
}