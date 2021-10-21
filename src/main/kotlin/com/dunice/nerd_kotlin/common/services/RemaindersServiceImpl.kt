package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.services.generation.MessageGenerationService
import com.dunice.nerd_kotlin.common.types.RemainderTask
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoField
import java.util.*

@Service
class RemaindersServiceImpl(
    val mongoTemplate: MongoTemplate,
    val messageGenerationService: MessageGenerationService
    ) : RemaindersService {

    var scheduledTasks = emptyList<RemainderTask>().toMutableList()

    @Scheduled(cron = "0 0 5 * * *")
    override fun startCrons() {
        val currentDate = OffsetDateTime.now()
        val todayDate = currentDate.with(ChronoField.MILLI_OF_SECOND, 0L)
            .with(ChronoField.HOUR_OF_DAY, 5L)
            .with(ChronoField.SECOND_OF_MINUTE, 0L)
            .with(ChronoField.MINUTE_OF_HOUR, 0L)
        val temporalCriteria = if (currentDate.isEqual(todayDate) || currentDate.isAfter(todayDate))
            Criteria.where("dateTime").gt(currentDate.toInstant())
                .lt(todayDate.plusHours(24L).toInstant())
            else
            Criteria.where("dateTime").gt(currentDate.toInstant())
                .lt(todayDate.toInstant())
        val todayExams = mongoTemplate.find(
            Query().addCriteria(temporalCriteria).addCriteria(Criteria.where("isSent").`is`(false))
            , RemainderDocument::class.java,"remainders")
        todayExams.forEach {
            val task = RemainderTask(messageGenerationService, mongoTemplate, it)
            scheduledTasks.add(task)
            Timer(false).schedule(task, it.dateTime.toEpochMilli() - Instant.now().toEpochMilli())
        }
    }

    override fun refreshCrons() {
        scheduledTasks.forEach { it.cancel() }
        scheduledTasks.clear()
        this.startCrons()
    }

    override fun getCurrentCrons() {
        scheduledTasks.forEach {
            println(messageGenerationService.generateRemainderDescription(it.remainderDocument))
        }
    }

    @EventListener(classes =  [ContextRefreshedEvent::class] )
    fun handleCron() {
        this.startCrons()
    }
}