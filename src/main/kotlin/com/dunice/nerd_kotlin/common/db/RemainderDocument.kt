package com.dunice.nerd_kotlin.common.db

import com.dunice.nerd_kotlin.common.errors.BLANK_INTERVIEWER
import com.dunice.nerd_kotlin.common.errors.BLANK_ROOM
import com.dunice.nerd_kotlin.common.errors.BLANK_SUBJECT
import com.dunice.nerd_kotlin.common.errors.EMAIL_NOT_VALID_MESSAGE
import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Document(collection = "remainders")
class RemainderDocument (


    var dateTime: Instant,

    var studentEmail: String,

    var subject: String,

    var room: String,

    var interviewerEmail: String,

    var assistantEmail: String?
        ) {

    @Id
    lateinit var id: String

    var isSent : Boolean = false
}