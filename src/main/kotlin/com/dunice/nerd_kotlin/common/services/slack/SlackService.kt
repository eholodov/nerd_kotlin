package com.dunice.nerd_kotlin.common.services.slack

import com.dunice.nerd_kotlin.common.db.MemberDocument
import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.types.ExamDTO
import java.util.*

interface SlackService {

    fun getNamesByEmail(vararg emails: String): Map<String, MemberDocument>

    fun sendMessage(email: String, message: String)

    fun checkEmail(email: String): Boolean

    fun sendLogMessage(message: String)
}