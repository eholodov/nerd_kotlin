package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.TestMessageGenerationServiceImpl
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping
class TestControllerImpl (val testMessageGenerationServiceImpl: TestMessageGenerationServiceImpl) : TestController {
    override fun testInfo(examDataDTO: List<ExamDTO>) {
        testMessageGenerationServiceImpl.generateInterviewerOrAssistantMessage(examDataDTO)
        examDataDTO.forEach {
            println()
            testMessageGenerationServiceImpl.generateStudentMessage(it)
            println()
        }

    }
}