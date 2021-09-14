package com.dunice.nerd_kotlin.common.types

enum class GoogleSheetFields(val index: Int, val alias: String) {
    PARTICIPANT_FULL_NAME(0, "ФИО"),
    DEPARTMENT(1, "Отдел"),
    SUBJECT(2, "Что сдает"),
    COLUMN(3, "Колонка"),
    INTERVIEWER_FULL_NAME(4, "Кто принимает"),
    AMOUNT_OF_ATTEMPTS(5, "Попыток было"),
    LAST_ATTEMPT_DATE(6, "Дата крайней попытки"),
    TIME(7, "Время"),
    DATE(8, "Дата"),
    ROOM(9, "Где"),
    ASSISTANT_NAME(10, "Ассистент"),
}