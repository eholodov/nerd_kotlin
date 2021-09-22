package com.dunice.nerd_kotlin.common

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "slackIds")
class Member(
    @Indexed(unique = true)
    var email: String?,

    @Indexed(unique = true)
    var slackId: String,
) {
    @Id
    lateinit var id: String
}