package com.dunice.nerd_kotlin.common.utils

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoField

fun getNumberOfWeek(): Int = LocalDateTime.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)
