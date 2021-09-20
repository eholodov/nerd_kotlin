package com.dunice.nerd_kotlin.common.types

class InterviewerCardInfo {

    lateinit var department: String
    lateinit var date: String
    lateinit var time: String
    lateinit var nameStudent: String
    lateinit var subject: String
    lateinit var room: String
    var isAssistant: Boolean = false
    lateinit var mainInterviewer: String

}

fun convertFromSheetCard(cardInfo: SpreadSheetCardInfo): InterviewerCardInfo {
    val interviewerCardInfo = InterviewerCardInfo()
    interviewerCardInfo.department = cardInfo.department
    interviewerCardInfo.date = cardInfo.date
    interviewerCardInfo.room = cardInfo.room
    interviewerCardInfo.time = cardInfo.time
    interviewerCardInfo.nameStudent = cardInfo.nameStudent
    interviewerCardInfo.subject = cardInfo.subject
    return interviewerCardInfo
}
