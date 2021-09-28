package com.dunice.nerd_kotlin.common.types

import com.dunice.nerd_kotlin.common.errors.*
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class ExamDTO(

    @field:NotNull(message = BLANK_DATETIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssX")
    val datetime: OffsetDateTime,

    @field:Pattern(regexp = "[a-z]+(@dunice\\.net)", message = EMAIL_NOT_VALID_MESSAGE)
    @field:NotBlank(message = BLANK_EMAIL)
    @JsonProperty("student_email")
    val studentEmail: String,

    @field:NotBlank(message = BLANK_SUBJECT)
    val subject: String,

    @field:NotBlank(message = BLANK_ROOM)
    val room: String,

    @field:Pattern(regexp = "[a-z]+(@dunice\\.net)", message = EMAIL_NOT_VALID_MESSAGE)
    @field:NotBlank(message = BLANK_INTERVIEWER)
    @JsonProperty("interviewer_email")
    val interviewerEmail: String,

    @field:Pattern(regexp = "[a-z]+(@dunice\\.net)", message = EMAIL_NOT_VALID_MESSAGE)
    @JsonProperty("assistant_email")
    val assistantEmail: String?
)