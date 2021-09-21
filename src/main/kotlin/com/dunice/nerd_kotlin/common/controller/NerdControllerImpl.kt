package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.NerdService
import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class NerdControllerImpl (val service: NerdService) : NerdController {

    override fun getDataFromRiseUp(@RequestBody examDataDTO: ExamDataDTO) {
        service.getDataFromRiseUp(examDataDTO)
    }
}