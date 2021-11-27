package com.dunice.nerd_kotlin.academyReminder.dto

import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@Validated
data class AddRemindersDto(
    @field:NotNull(message = "Department cannot be null")
    val department: String,

    val data: List<List<String>>
)
