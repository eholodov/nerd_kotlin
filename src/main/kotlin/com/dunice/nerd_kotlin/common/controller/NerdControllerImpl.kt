package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.NerdService
import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Validated
class NerdControllerImpl (val service: NerdService) : NerdController {

    override fun getDataFromRiseUp(@Validated @RequestBody examDataDTO: ExamDataDTO) {
        service.getDataFromRiseUp(examDataDTO)
    }
}