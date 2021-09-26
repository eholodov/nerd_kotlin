package com.dunice.nerd_kotlin.common.types

import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.services.MessageGenerationService
import java.util.*

class RemainderTask(private val messageGenerationService: MessageGenerationService, val remainderDocument: RemainderDocument)
    : TimerTask() {
    override fun run() {
        messageGenerationService.generateRemainderMessage(remainderDocument)
    }
}