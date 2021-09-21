package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping
interface NerdController {

    @PostMapping
    public fun getDataFromRiseUp(examDataDTO: ExamDataDTO)
}