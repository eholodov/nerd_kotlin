package com.dunice.nerd_kotlin.common.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
const val academyReminderDocumentCollectionName = "academyReminders"
@Document(collection = academyReminderDocumentCollectionName)
data class AcademyReminderDocument(
    val dateToSend: Instant,
    val message: String,
    val slackId: String,
    val department: String,
    val week: Int,
    var isSent: Boolean = false,
    ) {
    @Id
    lateinit var id: String
}
