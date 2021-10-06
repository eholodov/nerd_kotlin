package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.db.RemaindersRepository
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.errors.SAME_EMAILS
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val remindersRepository: RemaindersRepository,
                       val messageGenerationService: MessageGenerationService,
                       val remaindersService: RemaindersService)
    : NerdService {

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
        messageGenerationService.generateInterviewerOrAssistantMessage(examDTO)
        examDTO.forEach{messageGenerationService.generateStudentMessage(it)}
    }

    override fun generateReminders(examDataDTO: List<ExamDTO>) {
        val entities = examDataDTO.map { RemainderDocument(dateTime = it.datetime.minusMinutes(10L).toInstant(),
            assistantEmail = it.assistantEmail, interviewerEmail = it.interviewerEmail, studentEmail = it.studentEmail, subject = it.subject, room = it.room) }
        remindersRepository.saveAll(entities)
        remaindersService.startCrons()

    }
}

