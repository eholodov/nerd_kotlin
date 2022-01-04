package com.dunice.nerd_kotlin.common.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "weeklySendDocument")
class WeeklyIsSendDocument (

    var weekNumber : String
        ){
    @Id
    lateinit var id: String
}