package com.dunice.nerd_kotlin.common.types

data class InterviewerCardInfo (
    val department: String,
    val date: String,
    val time: String,
    val nameStudent: String,
    val subject: String,
    val room: String,
    var isAssistant: Boolean = false,
    var mainInterviewer: String
        ) {

}

