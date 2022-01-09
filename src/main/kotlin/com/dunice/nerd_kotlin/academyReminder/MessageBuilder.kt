package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.common.utils.getCyrillicDayOfWeek
import java.time.DayOfWeek


data class MessageBuilder(
    val parts: StringBuilder = StringBuilder()
) {

    fun greetings(name: String): MessageBuilder {
        parts.append("Привет, $name! :wave::skin-tone-2:")
        return this
    }

    fun hey(): MessageBuilder {
        parts.append("Хэй! :wave:")
        return this
    }

    fun passEventReminder(eventType: String, interviewer: String, notifyBeforeMinutes: Long): MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $notifyBeforeMinutes минут у тебя ${eventType.lowercase()} у $interviewer")
        return this
    }

    fun helpWithEventReminder(eventType: String, trainee: String, notifyBeforeMinutes: Long): MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $notifyBeforeMinutes минут тебе нужно провести ${eventType.lowercase()} у $trainee")
        return this
    }

    fun checkEventReminder(eventType: String, trainee: String, interviewer: String, notifyBeforeMinutes: Long) : MessageBuilder {
        parts.append("\uD83D\uDCDAЧерез $notifyBeforeMinutes минут ${eventType.lowercase()} у $trainee с $interviewer")
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

    fun doubleNextLine(): MessageBuilder {
        parts.append("\n\n")

        return this
    }

    fun passEvent(eventType: String, interviewer: String, time: String) : MessageBuilder {
        parts.append("\uD83D\uDCDAПройти ${eventType.lowercase()} у $interviewer")
        addOptionalTime(time)
        return this
    }

    fun passWeeklyEvent(eventType: String, interviewer: String, time: String) : MessageBuilder {
        parts.append("> \uD83D\uDCDAПройти ${eventType.lowercase()} у $interviewer")
        addOptionalTime(time)
        return this
    }

    fun helpWithEvent(eventType: String, trainee: String, time: String) : MessageBuilder {
        parts.append("\uD83D\uDCDAПровести ${eventType.lowercase()} у $trainee")
        addOptionalTime(time)

        return this
    }

    fun conductWeeklyEvent(eventType: String, trainee: String, time: String) : MessageBuilder {
        parts.append("> \uD83D\uDCDAПровести ${eventType.lowercase()} у $trainee")
        addOptionalTime(time)

        return this
    }

    fun watchEvent(eventType: String, trainee: String, interviewer: String, time: String): MessageBuilder {
        parts.append("\uD83D\uDCDA$eventType у $trainee проводит $interviewer")
        addOptionalTime(time)

        return this
    }

    fun watchWeeklyEvent(eventType: String, trainee: String, interviewer: String, time: String): MessageBuilder {
        parts.append("> \uD83D\uDCDA$eventType у $trainee, проводит $interviewer")
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

    fun weeklyEvents(): MessageBuilder {
        parts.append("Расписание событий на эту неделю:")

        return this
    }

    fun addDayOfWeek(dayOfWeek: DayOfWeek, fullDate: String): MessageBuilder{
        parts.append(getCyrillicDayOfWeek(dayOfWeek), " ($fullDate)")

        return this
    }

    private fun addOptionalTime(time: String) {
        if (time != "00:00") {
            parts.append(" в $time")
        }
    }

    fun diffMessage(): MessageBuilder {
        parts.append("Изменения в расписании!")

        return this
    }

    fun diffRemovedEvents(): MessageBuilder {
        parts.append("*Удаленные события!*")

        return this
    }

    fun diffAddedEvents(): MessageBuilder {
        parts.append("*Добавленные события!*")

        return this
    }
    fun build(): String = parts.toString()


}