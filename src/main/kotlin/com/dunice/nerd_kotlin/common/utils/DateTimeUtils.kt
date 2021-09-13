package com.dunice.nerd_kotlin.common.utils

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoField

@Component
class DateTimeUtils {

    fun getNumberOfWeek(): Int {
        var date = LocalDateTime.now()
        return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
    }
}