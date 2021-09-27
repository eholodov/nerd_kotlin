package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.NerdService

import org.springframework.validation.annotation.Validated
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/matrix-schedule")
@Validated
class MatrixScheduleControllerImpl (val service: NerdService) : MatrixScheduleController {

    override fun getDataFromRiseUp(@RequestBody examDataDTO: List<ExamDTO>) {
        service.getDataFromRiseUp(examDataDTO)
    }
}