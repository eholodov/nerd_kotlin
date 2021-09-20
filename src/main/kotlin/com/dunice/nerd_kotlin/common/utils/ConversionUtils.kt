package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.InterviewerCardInfo
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo

fun SpreadSheetCardInfo.toInterviewCardInfo() = InterviewerCardInfo(
    department = department,
    date = date,
    room = room,
    time = time,
    nameStudent = nameStudent,
    subject = subject,
    mainInterviewer = ""
)