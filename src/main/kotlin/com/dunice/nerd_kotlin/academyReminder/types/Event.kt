package com.dunice.nerd_kotlin.academyReminder.types

import java.time.OffsetDateTime

data class Event(
    val date: OffsetDateTime,
    val trainee: String,
    val eventType: String,
    val interviewer: String,
    val recipients: List<String>,
    val lvl: String,
    val location: String,
    val topic: String
)