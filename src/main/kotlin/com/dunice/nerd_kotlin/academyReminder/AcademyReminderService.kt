package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderService
import com.dunice.nerd_kotlin.academyReminder.types.Event
import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument
import com.dunice.nerd_kotlin.common.db.AcademyReminderRepository
import com.dunice.nerd_kotlin.common.db.MembersRepository
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset


@Service
class AcademyReminderService(
    private val membersRepository: MembersRepository,
    private val academyReminderRepository: AcademyReminderRepository,
    private val academySchedulerServiceImpl: AcademySchedulerServiceImpl,
    private val slackServiceImpl: SlackServiceImpl,
    private val weeklyReminderService: WeeklyReminderService
) {

    fun addReminders(data: List<List<String>>, department: String) {

        val events = data.fold(mutableListOf<Event>()) { acc, item ->

            val parsedDate = OffsetDateTime.parse(item[0])

            var recipients = item[4].split(" ")

            if (recipients.size > 1 && recipients.size % 2 != 0) {
                throw RuntimeException("Полу" +
                        "чатели указаны в некорректном формате!")
            }

            if (recipients.size == 1) {
                recipients = listOf()
            }

            val recipientsList = mutableListOf<String>()

            for (i in recipients.indices step 2) {
                recipientsList.add("${recipients[i]} ${recipients[i+1]}")
            }

            recipientsList.add(item[1])
            recipientsList.add(item[3])

            acc.add(Event(parsedDate, item[1], item[2], item[3], recipientsList ))
            acc
        }

        academyReminderRepository.deleteAllByIsSentAndDepartment(false, department)
        academySchedulerServiceImpl.cancelScheduledTasksByDepartment(department)
        weeklyReminderService.generateWeeklyReminders(events, department);
        val reminders = generateAndSaveAcademyReminders(events, department)

        academySchedulerServiceImpl.schedule(reminders, department)
    }
// For testing
//    @EventListener(classes = [ContextRefreshedEvent::class])
//    fun handleMultipleEvents() {
//
//        this.addReminders(listOf(
//            listOf("2021-11-26T09:00:00.000Z","Дмитрий Коровяков","Предопрос", "Максим Сметанкин", "Евгений Холодов"),
//            listOf("2021-11-23T21:00:00.000Z","Геннадий Герасименков","Предопрос","Максим Сметанкин", "Евгений Холодов"),
//            listOf("2021-11-26T21:00:00.000Z","Кирилл Коломейцев","Опрос","Валерий Попов", "Евгений Холодов Мария Власова"),
//            listOf("2021-11-24T21:00:00.000Z","Дмитрий Коровяков","Опрос","Валерий Попов", "Евгений Холодов"),
//            listOf("2021-11-25T21:00:00.000Z","Максим Сметанкин","Опрос","Валерий Попов", "Евгений Холодов"),
//        ), "java")

//        this.addReminders(listOf(
//            listOf("2021-11-29T13:37:00.000Z","Евгений Холодов","Предопрос111", "Евгений Холодов", "Евгений Холодов"),
//            listOf("2021-11-29T13:36:00.000Z","Евгений Холодов","Предопрос222","Евгений Холодов", "Евгений Холодов"),
//            listOf("2021-11-29T13:35:00.000Z","Стажер Холодов","Опрос","Интервьюер Холодов", "Евгений Холодов"),
//        ), "java")
//    }

    private fun generateAndSaveAcademyReminders(events: List<Event>, department: String): List<AcademyReminderDocument> {
        val fullNameSlackIdsMap = slackServiceImpl.getSlackIds(events)
        val now = OffsetDateTime.now()

        val reminders = listOf(
            generateDailyReminders(events, fullNameSlackIdsMap, department, now),
            generateReminders(events, fullNameSlackIdsMap, department, now)
        ).flatten()

        academyReminderRepository.saveAll(reminders)

        return reminders

    }

    private fun generateReminders(
        events: List<Event>,
        fullNameSlackIdsMap: MutableMap<String, String>,
        department: String,
        now: OffsetDateTime
    ): List<AcademyReminderDocument> {

        return events
            .filter { it.date > now && (it.date.hour != 21 || it.date.minute != 0)}
            .fold(mutableListOf<AcademyReminderDocument>()) {acc, event ->

            event.recipients.forEach {
                val messageBuilder = MessageBuilder().hey().nextLine()
                val notifyBeforeMinutes: Long = if (now > event.date.minusMinutes(academyReminderNotifyBeforeMinutes)) {
                    (event.date.toEpochSecond() - now.toEpochSecond()) / 60
                } else {
                    academyReminderNotifyBeforeMinutes
                }
                when (it) {
                    event.trainee -> {
                        messageBuilder
                            .passEventReminder(event.eventType,event.interviewer, notifyBeforeMinutes)
                    }

                    event.interviewer -> {
                        messageBuilder
                            .helpWithEventReminder(event.eventType, event.trainee, notifyBeforeMinutes)
                    }

                    else -> {
                        messageBuilder
                            .checkEventReminder(event.eventType, event.trainee, event.interviewer, notifyBeforeMinutes)
                    }
                }

                acc.add(AcademyReminderDocument(
                    event.date.minusMinutes(notifyBeforeMinutes).toInstant(),
                    messageBuilder.build(),
                    fullNameSlackIdsMap.getOrElse(it) {
                        throw RuntimeException("$it не была найден в fullNameSlackIdsMap")
                    },
                    department)
                )
            }
            acc
        }.toList()

    }

    private fun generateDailyReminders(
        events: List<Event>,
        fullNameSlackIdsMap: MutableMap<String, String>,
        department: String,
        now: OffsetDateTime
    ): List<AcademyReminderDocument> {

        return events
            .fold(mutableMapOf<String, MutableMap<OffsetDateTime, MutableList<Event>>>()) {acc, event ->

            val dateToSend = generateDateToSend(event.date)

            event.recipients.forEach {
                acc.putIfAbsent(it, mutableMapOf())
                val recipient = acc[it]
                recipient!!.putIfAbsent(dateToSend, mutableListOf())
                recipient[dateToSend]!!.add(event)
            }

            acc
        }.toList().fold(mutableListOf<AcademyReminderDocument>()) {acc, eventPair ->
            val name = eventPair.first
            val datesEvents = eventPair.second

            datesEvents.toList().forEach {
                val lEvents = it.second
                val dateToSend = it.first

                if (dateToSend < now) {
                    return@forEach
                }

                val messageBuilder = MessageBuilder()
                    .greetings(name.split(" ")[0])
                    .nextLine()
                    .todayEvents()
                    .nextLine()

                lEvents.forEachIndexed {index, event ->
                    val dateInPlus3 = event.date.plusHours(3)
                    val hour = dateInPlus3.hour.toString()
                    val minute = dateInPlus3.minute.toString()
                    val time = "${if (hour.length == 1) "0${hour}" else hour}:${if (minute.length == 1) "0${minute}" else minute}"

                    when (name) {
                        event.trainee -> {
                            messageBuilder
                                .passEvent(event.eventType,event.interviewer, time)
                        }

                        event.interviewer -> {
                            messageBuilder
                                .helpWithEvent(event.eventType, event.trainee, time)
                        }

                        else -> {
                            messageBuilder
                                .checkEvent(event.eventType, event.trainee, event.interviewer, time)
                        }
                    }

                    if (index != lEvents.lastIndex) {
                        messageBuilder.nextLine()
                    }
                }

                val message = messageBuilder.build()

                acc.add(AcademyReminderDocument(
                    dateToSend.toInstant(),
                    message,
                    fullNameSlackIdsMap.getOrElse(name) {
                        throw RuntimeException("$name не была найден в fullNameSlackIdsMap")
                    },
                    department
                ))
            }

            acc
        }.toList()
    }

    private fun generateDateToSend(dateOfElem: OffsetDateTime): OffsetDateTime {

        // Added 3 hours for understanding is it must be sent in the next day
        val dateOfElemPlus3Hours = dateOfElem.plusHours(3)
        val validDate = if ( dateOfElemPlus3Hours.dayOfMonth > dateOfElem.dayOfMonth) dateOfElemPlus3Hours else dateOfElem

        return OffsetDateTime.of(
            validDate.year,
            validDate.monthValue,
            validDate.dayOfMonth,
            6,
            0,
            0,
            0,
            ZoneOffset.UTC
        )
    }
}