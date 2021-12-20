package com.dunice.nerd_kotlin.AcademyReminders;

import com.dunice.nerd_kotlin.academyReminder.MessageBuilder;
import com.dunice.nerd_kotlin.academyReminder.types.Event;
import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class WeeklyReminderService {

    public List<AcademyReminderDocument> generateWeeklyReminders(List<Event> events, String department) {

        MessageBuilder messageBuilder = new MessageBuilder();

        return null;
    }

}

//    private fun generateDailyReminders(
//        events: List<Event>,
//        fullNameSlackIdsMap: MutableMap<String, String>,
//        department: String,
//        now: OffsetDateTime
//    ): List<AcademyReminderDocument> {
//
//        return events
//            .fold(mutableMapOf<String, MutableMap<OffsetDateTime, MutableList<Event>>>()) {acc, event ->
//
//            val dateToSend = generateDateToSend(event.date)
//
//            event.recipients.forEach {
//                acc.putIfAbsent(it, mutableMapOf())
//                val recipient = acc[it]
//                recipient!!.putIfAbsent(dateToSend, mutableListOf())
//                recipient[dateToSend]!!.add(event)
//            }
//
//            acc
//        }.toList().fold(mutableListOf<AcademyReminderDocument>()) {acc, eventPair ->
//            val name = eventPair.first
//            val datesEvents = eventPair.second
//
//            datesEvents.toList().forEach {
//                val lEvents = it.second
//                val dateToSend = it.first
//
//                if (dateToSend < now) {
//                    return@forEach
//                }
//
//                val messageBuilder = MessageBuilder()
//                    .greetings(name.split(" ")[0])
//                    .nextLine()
//                    .todayEvents()
//                    .nextLine()
//
//                lEvents.forEachIndexed {index, event ->
//                    val dateInPlus3 = event.date.plusHours(3)
//                    val hour = dateInPlus3.hour.toString()
//                    val minute = dateInPlus3.minute.toString()
//                    val time = "${if (hour.length == 1) "0${hour}" else hour}:${if (minute.length == 1) "0${minute}" else minute}"
//
//                    when (name) {
//                        event.trainee -> {
//                            messageBuilder
//                                .passEvent(event.eventType,event.interviewer, time)
//                        }
//
//                        event.interviewer -> {
//                            messageBuilder
//                                .helpWithEvent(event.eventType, event.trainee, time)
//                        }
//
//                        else -> {
//                            messageBuilder
//                                .checkEvent(event.eventType, event.trainee, event.interviewer, time)
//                        }
//                    }
//
//                    if (index != lEvents.lastIndex) {
//                        messageBuilder.nextLine()
//                    }
//                }
//
//                val message = messageBuilder.build()
//
//                acc.add(AcademyReminderDocument(
//                    dateToSend.toInstant(),
//                    message,
//                    fullNameSlackIdsMap.getOrElse(name) {
//                        throw RuntimeException("$name не была найден в fullNameSlackIdsMap")
//                    },
//                    department
//                ))
//            }
//
//            acc
//        }.toList()
//    }