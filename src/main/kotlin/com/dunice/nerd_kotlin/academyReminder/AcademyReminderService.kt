package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.academyReminder.types.Event
import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument
import com.dunice.nerd_kotlin.common.db.AcademyReminderRepository
import com.dunice.nerd_kotlin.common.db.MembersRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset


@Service
class AcademyReminderService(
    val membersRepository: MembersRepository,
    val academyReminderRepository: AcademyReminderRepository,
    val academySchedulerServiceImpl: AcademySchedulerServiceImpl
) {

    fun addReminders(data: List<List<String>>, department: String) {
        val events = data.fold(mutableListOf<Event>()) { acc, item ->

            val parsedDate = OffsetDateTime.parse(item[0])

            val recipients = item[4].split(" ")

            if (recipients.size % 2 != 0) {
                throw RuntimeException("Полу" +
                        "чатели указаны в некорректном формате!")
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
        val reminders = generateAndSaveAcademyReminders(events, department)

        academySchedulerServiceImpl.schedule(reminders, department)
    }
// For testing
//    @EventListener(classes = [ContextRefreshedEvent::class])
//    fun handleMultipleEvents() {

//        this.addReminders(listOf(
//            listOf("2021-11-27T09:00:00.000Z","Дмитрий Коровяков","Предопрос", "Максим Сметанкин", "Евгений Холодов"),
//            listOf("2021-11-27T21:00:00.000Z","Геннадий Герасименков","Предопрос","Максим Сметанкин", "Евгений Холодов"),
//            listOf("2021-11-27T21:00:00.000Z","Кирилл Коломейцев","Опрос","Валерий Попов", "Евгений Холодов"),
//        ), "java")

//        this.addReminders(listOf(
//            listOf("2021-11-29T13:37:00.000Z","Евгений Холодов","Предопрос111", "Евгений Холодов", "Евгений Холодов"),
//            listOf("2021-11-29T13:36:00.000Z","Евгений Холодов","Предопрос222","Евгений Холодов", "Евгений Холодов"),
//            listOf("2021-11-29T13:35:00.000Z","Стажер Холодов","Опрос","Интервьюер Холодов", "Евгений Холодов"),
//        ), "java")
//    }

    private fun generateAndSaveAcademyReminders(events: List<Event>, department: String): List<AcademyReminderDocument> {
        val fullNameSlackIdsMap = getSlackIds(events)
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
                when (it) {
                    event.trainee -> {
                        messageBuilder
                            .passEventReminder(event.eventType,event.interviewer)
                    }

                    event.interviewer -> {
                        messageBuilder
                            .helpWithEventReminder(event.eventType, event.trainee)
                    }

                    else -> {
                        messageBuilder
                            .checkEventReminder(event.eventType, event.trainee, event.interviewer)
                    }
                }

                acc.add(AcademyReminderDocument(
                    event.date.minusMinutes(academyReminderNotifyBeforeMinutes).toInstant(),
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
            .fold(mutableMapOf<String, MutableMap<String, MutableList<Event>>>()) {acc, event ->

            val keyDate = "${event.date.dayOfMonth}${event.date.month}"

            event.recipients.forEach {
                acc.putIfAbsent(it, mutableMapOf())
                val recipient = acc[it]
                recipient!!.putIfAbsent(keyDate, mutableListOf())
                recipient[keyDate]!!.add(event)
            }

            acc
        }.toList().fold(mutableListOf<AcademyReminderDocument>()) {acc, eventPair ->
            val name = eventPair.first
            val datesEvents = eventPair.second

            datesEvents.toList().forEach {
                val lEvents = it.second
                val dateOfElem = lEvents[0].date
                val dateToSend = generateDateToSend(dateOfElem)

                if (dateToSend < now) {
                    return acc
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

    private fun getSlackIds(data: List<Event>): MutableMap<String, String> {
        val recipients = data.fold(mutableSetOf<String>()) { acc, item ->

            item.recipients.forEach {
                acc.add(it)
            }
            acc
        }

        val slackIdsFullName = membersRepository.findByFullNameIn(recipients.toList())

        if (recipients.size > slackIdsFullName.size) {
            val diff = recipients.filter { recipient ->
                val element = slackIdsFullName.find { it.getFullName() == recipient  }

                element == null
            }

            throw RuntimeException("Не были найдены slack id для пользователе ${diff.joinToString(" ")}")
        }


        return slackIdsFullName.fold(mutableMapOf()) { acc, item ->

            acc[item.getFullName()] = item.getSlackId()
            acc
        }
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