package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
interface TestController {

    @PostMapping
    fun testInfo(examDataDTO: List<ExamDTO>)

}