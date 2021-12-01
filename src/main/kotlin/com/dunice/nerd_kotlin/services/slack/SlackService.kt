package com.dunice.nerd_kotlin.services.slack

import com.dunice.nerd_kotlin.common.db.MemberDocument

interface SlackService {

    fun getNamesByEmail(vararg emails: String): Map<String, MemberDocument>

    fun sendMessage(email: String, message: String)

    fun checkEmail(email: String): Boolean

    fun sendLogMessage(message: String)
}