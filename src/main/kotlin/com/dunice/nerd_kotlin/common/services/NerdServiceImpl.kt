package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.db.RemaindersRepository
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val slackService: SlackService, val remindersRepository: RemaindersRepository, val messageGenerationService: MessageGenerationService) : NerdService {

    override fun getDataFromRiseUp(examDTO: List<ExamDTO>) {
        val entities = examDTO.map { RemainderDocument(dateTime = it.datetime.minusMinutes(10L).toInstant(),
            assistantEmail = it.assistantEmail, interviewerEmail = it.interviewerEmail, studentEmail = it.studentEmail, subject = it.subject, room = it.room) }
        remindersRepository.saveAll(entities)
        messageGenerationService.generateInterviewerOrAssistantMessage(examDTO)
        examDTO.forEach{messageGenerationService.generateStudentMessage(it)}
    }
}

