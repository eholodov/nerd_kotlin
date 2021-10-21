package com.dunice.nerd_kotlin.common.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "slackIds")
class MemberDocument(
    @Indexed(unique = true)
    var email: String?,

    @Indexed(unique = true)
    var slackId: String,

    @Indexed
    var fullName: String
) {
    @Id
    lateinit var id: String
}