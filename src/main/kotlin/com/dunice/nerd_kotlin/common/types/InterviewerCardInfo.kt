package com.dunice.nerd_kotlin.common.types

data class InterviewerCardInfo (
    val department: String,
    val date: String,
    val time: String,
    val nameStudent: String,
    val subject: String,
    val room: String,
    var isAssistant: Boolean = false,
    var mainInterviewer: String? = null
        ) {

}

fun SpreadSheetCardInfo.toInterviewCardInfo() = InterviewerCardInfo(
    department = department,
    date = date,
    room = room,
    time = time,
    nameStudent = nameStudent,
    subject = subject
)

