package com.dunice.nerd_kotlin.common.types

import java.time.OffsetDateTime

data class ExamDataDTO(
    val datetime: OffsetDateTime,
    val nameStudent: String,
    val subject: String,
    val room: String,
    val email: String,
    val interviewer: String
)