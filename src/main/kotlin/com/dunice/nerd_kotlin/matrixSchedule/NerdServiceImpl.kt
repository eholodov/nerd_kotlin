package com.dunice.nerd_kotlin.matrixSchedule

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.db.RemaindersRepository
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.errors.SAME_EMAILS
import com.dunice.nerd_kotlin.common.types.ExamDTO
import com.dunice.nerd_kotlin.services.MessageGenerationService
import com.dunice.nerd_kotlin.services.SlackService
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val slackService: SlackService, val remindersRepository: RemaindersRepository, val messageGenerationService: MessageGenerationService) :
    NerdService {

    override fun getDataFromRiseUp(examDTO: List<ExamDTO>) {
        examDTO.toMutableList()
        examDTO.forEach {
            if (it.studentEmail == it.interviewerEmail
                || it.studentEmail == it.assistantEmail
                || it.interviewerEmail == it.assistantEmail
            ) {
                throw CustomException(SAME_EMAILS)
            }
        }

        val entities = examDTO.map { RemainderDocument(dateTime = it.datetime.minusMinutes(10L).toInstant(),
            assistantEmail = it.assistantEmail, interviewerEmail = it.interviewerEmail, studentEmail = it.studentEmail, subject = it.subject, room = it.room) }
        remindersRepository.saveAll(entities)
        messageGenerationService.generateInterviewerOrAssistantMessage(examDTO)
        examDTO.forEach{messageGenerationService.generateStudentMessage(it)}
    }
}

