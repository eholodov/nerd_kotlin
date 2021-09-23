package com.dunice.nerd_kotlin.common.types

import java.time.OffsetDateTime

data class ExamDTO(
    val datetime: OffsetDateTime,
    val studentEmail: String,
    val subject: String,
    val room: String,
    val interviewerEmail: String,
    val assistantEmail: String?
)