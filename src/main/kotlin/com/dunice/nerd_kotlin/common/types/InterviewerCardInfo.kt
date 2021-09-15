package com.dunice.nerd_kotlin.common.types

data class InterviewerCardInfo(val card: SpreadSheetCardInfo) {

    lateinit var mainInterviewer: String
    var isAssistant: Boolean = false

}
