package com.dunice.nerd_kotlin.common

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "slackIds")
class Member {

    @Id
    lateinit var id: String

    @Indexed(unique = true)
    lateinit var email: String

    @Indexed(unique = true)
    lateinit var slackId: String

}