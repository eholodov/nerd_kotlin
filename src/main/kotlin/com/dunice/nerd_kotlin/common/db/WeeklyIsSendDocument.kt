package com.dunice.nerd_kotlin.common.db

import com.dunice.nerd_kotlin.academyReminder.types.Event
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "weeklySentDocument")
class WeeklySentDocument(
    var weekNumber: String,
    var department: String,
    var events: List<Event>,
    var isSent: Boolean?
){
    @Id
    lateinit var id: String
}