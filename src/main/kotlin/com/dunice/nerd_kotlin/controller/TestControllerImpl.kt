package com.dunice.nerd_kotlin.controller

import com.dunice.nerd_kotlin.common.db.RemaindersRepository
import com.dunice.nerd_kotlin.services.generation.TestMessageGenerationServiceImpl
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/matrix-scheduler")
class TestControllerImpl (val testMessageGenerationServiceImpl: TestMessageGenerationServiceImpl, val remaindersRepository: RemaindersRepository) : TestController {
    override fun testInfo(@RequestBody examDataDTO: List<ExamDTO>) : ResponseEntity<MutableList<String>> {
        testMessageGenerationServiceImpl.generateInterviewerOrAssistantMessage(examDataDTO)
        examDataDTO.forEach {
            testMessageGenerationServiceImpl.generateStudentMessage(it)
        }
        remaindersRepository.findAllByIsSent(false)
            .forEach {
                testMessageGenerationServiceImpl.generateRemainderMessage(it)
            }
        return ResponseEntity(testMessageGenerationServiceImpl.getMessageList(), HttpStatus.OK)
    }
}