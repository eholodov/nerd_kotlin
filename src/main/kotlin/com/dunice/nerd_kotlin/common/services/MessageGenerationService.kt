package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.MemberDocument
import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.types.ExamDTO

interface MessageGenerationService {

    fun generateStudentMessage(info : ExamDTO)

    fun generateInterviewerOrAssistantMessage(examDataDTO: List<ExamDTO>)

    fun generateRemainderMessage(remainderDocument: RemainderDocument)

    fun generateRemainderDescription(remainderDocument: RemainderDocument) : String

}