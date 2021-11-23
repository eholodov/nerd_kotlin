package com.dunice.nerd_kotlin.services

import com.dunice.nerd_kotlin.common.db.MemberDocument

interface SlackService {

    fun getNamesByEmail(vararg emails: String): Map<String, MemberDocument>

    fun sendMessage(email: String, message: String)
}