package com.dunice.nerd_kotlin.matrixSchedule

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/matrix-scheduler")
interface MatrixScheduleController {

    @PostMapping
    fun getDataFromRiseUp(examDataDTO: List<ExamDTO>)
}