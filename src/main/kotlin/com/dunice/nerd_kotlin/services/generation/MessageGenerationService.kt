package com.dunice.nerd_kotlin.services.generation

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.types.ExamDTO

interface MessageGenerationService {

    fun generateStudentMessage(info : ExamDTO)

    fun generateInterviewerOrAssistantMessage(examDataDTO: List<ExamDTO>)

    fun generateRemainderMessage(remainderDocument: RemainderDocument)

    fun generateRemainderDescription(remainderDocument: RemainderDocument) : String

}