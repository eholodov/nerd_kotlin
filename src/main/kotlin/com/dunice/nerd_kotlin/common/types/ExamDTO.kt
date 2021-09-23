package com.dunice.nerd_kotlin.common.types

import com.dunice.nerd_kotlin.common.errors.*
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class ExamDTO(

    @field:NotNull(message = BLANK_DATETIME)
    val datetime: OffsetDateTime,

    @field:NotBlank(message = BLANK_NAME)
    val studentEmail: String,

    @field:NotBlank(message = BLANK_SUBJECT)
    val subject: String,

    @field:NotBlank(message = BLANK_ROOM)
    val room: String,

    @field:Pattern(regexp = "[a-z]+(@dunice\\.net)", message = EMAIL_NOT_VALID_MESSAGE)
    @field:NotBlank(message = BLANK_EMAIL)
    val email: String,

    @field:NotBlank(message = BLANK_INTERVIEWER)
    val interviewerEmail: String,

    val assistantEmail: String?
)