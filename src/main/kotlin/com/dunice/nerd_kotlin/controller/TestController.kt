package com.dunice.nerd_kotlin.controller

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping


@RequestMapping("/matrix-scheduler")
interface TestController {

    @PostMapping("/test")
    fun testInfo(examDataDTO: List<ExamDTO>): ResponseEntity<MutableList<String>>

}