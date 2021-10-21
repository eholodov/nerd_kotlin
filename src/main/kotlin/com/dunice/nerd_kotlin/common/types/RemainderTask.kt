package com.dunice.nerd_kotlin.common.types

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.services.generation.MessageGenerationService
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import java.util.*
class RemainderTask(private val messageGenerationService: MessageGenerationService,
                    private val mongoTemplate: MongoTemplate,
                    val remainderDocument: RemainderDocument)
    : TimerTask() {

    override fun run() {
        messageGenerationService.generateRemainderMessage(remainderDocument)
        mongoTemplate.updateFirst(Query().addCriteria(Criteria.where("id").`is`(remainderDocument.id)),
            Update().set("isSent", true), RemainderDocument::class.java)
    }
}