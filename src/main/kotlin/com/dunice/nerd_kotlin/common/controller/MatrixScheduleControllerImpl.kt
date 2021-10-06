package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.NerdService

import org.springframework.validation.annotation.Validated
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/matrix-scheduler")
@Validated
class MatrixScheduleControllerImpl (val service: NerdService) : MatrixScheduleController {


    override fun getDataFromRiseUp(@RequestBody examDataDTO: List<ExamDTO>) {
        service.getDataFromRiseUp(examDataDTO)
    }

    override fun generateReminders(@RequestBody examDataDTO: List<ExamDTO>) {
        service.generateReminders(examDataDTO)
    }
}