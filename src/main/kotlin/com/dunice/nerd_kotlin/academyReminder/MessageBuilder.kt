package com.dunice.nerd_kotlin.academyReminder

import java.time.DayOfWeek

data class MessageBuilder(
    val parts: StringBuilder = StringBuilder()
) {

    fun greetings(name: String): MessageBuilder {
        parts.append("Привет, $name! :wave::skin-tone-2:")
        return this
    }
    fun weekly(eventType: String): MessageBuilder{
        parts.append("Твое расписание $eventType на эту неделю:")
        return this
    }
    fun dayOfWeek(dayOfWeek: String, data: String): MessageBuilder {
        parts.append("*$dayOfWeek ($data)*")
        return this
    }
    fun dayOfWeekMessaget(time: String, interviewer: String, topic: String, lvl: String, location: String): MessageBuilder{
        parts.append(":books: $topic [$lvl] $time $interviewer $location")
        return this
    }


    fun hey(): MessageBuilder {
        parts.append("Хэй! :wave:")
        return this
    }

    fun passEventReminder(eventType: String, interviewer: String): MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $academyReminderNotifyBeforeMinutes минут у тебя ${eventType.lowercase()} у $interviewer")
        return this
    }

    fun helpWithEventReminder(eventType: String, trainee: String): MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $academyReminderNotifyBeforeMinutes минут тебе нужно провести ${eventType.lowercase()} у $trainee")
        return this
    }

    fun checkEventReminder(eventType: String, trainee: String, interviewer: String) : MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $academyReminderNotifyBeforeMinutes минут ${eventType.lowercase()} у $trainee с $interviewer")
        return this
    }

    fun timing(event: String): MessageBuilder {
        parts.append("Через $academyReminderNotifyBeforeMinutes минут у тебя $event")

        return this
    }

    fun nextLine(): MessageBuilder {
        parts.append("\n")

        return this
    }

    fun passEvent(eventType: String, interviewer: String, time: String) : MessageBuilder {
        parts.append("\uD83D\uDCDAПройти ${eventType.lowercase()} у $interviewer")
        addOptionalTime(time)
        return this
    }

    fun helpWithEvent(eventType: String, trainee: String, time: String) : MessageBuilder {
        parts.append("\uD83D\uDCDAПровести ${eventType.lowercase()} у $trainee")
        addOptionalTime(time)

        return this
    }

    fun checkEvent(eventType: String, trainee: String, interviewer: String, time: String) : MessageBuilder {
        parts.append("\uD83D\uDCDA$eventType у $trainee вместе с $interviewer")

        addOptionalTime(time)

        return this
    }

    fun todayEvents(): MessageBuilder {
        parts.append("Твои события на сегодня")

        return this
    }

    private fun addOptionalTime(time: String) {
        if (time != "00:00") {
            parts.append(" в $time")
        }
    }

    fun build(): String = parts.toString()
}