package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.types.RemainderTask
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.schedule

@Service
class RemaindersServiceImpl(
    val mongoTemplate: MongoTemplate,
    val messageGenerationService: MessageGenerationService
    ) : RemaindersService {

    var scheduledTasks = emptyList<RemainderTask>().toMutableList()

    override fun startCrons() {
        val todayExams = mongoTemplate.find(
            Query().addCriteria(
                Criteria.where("dateTime").gt(Instant.now()).lt(Instant.now().plus(24L, ChronoUnit.HOURS)))
            , RemainderDocument::class.java,"remainders")
        todayExams.forEach {
            val task = RemainderTask(messageGenerationService, it)
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
}