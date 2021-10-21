package com.dunice.nerd_kotlin.common.utils

import java.time.DayOfWeek


fun getCyrillicDayOfWeek(dayOfWeek: DayOfWeek) : String =
    arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")[dayOfWeek.value - 1]