package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.RemainderDocument
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

    override fun startCrons() {
        val todayExams = mongoTemplate.find(
            Query().addCriteria(
                Criteria.where("dateTime").gt(Instant.now()).lt(Instant.now().plus(24L, ChronoUnit.HOURS)))
            , RemainderDocument::class.java,"remainders")
        todayExams.forEach {
            Timer(false).schedule(delay = it.dateTime.toEpochMilli() - Instant.now().toEpochMilli()) {
                messageGenerationService.generateRemainderMessage(it)
            }
        }
    }

    override fun refreshCrons() {
        TODO("Not yet implemented")
    }
}