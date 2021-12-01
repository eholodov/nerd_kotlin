package com.dunice.nerd_kotlin.services

import com.dunice.nerd_kotlin.common.types.ExamDTO

interface NerdService {

    fun getDataFromRiseUp(examDTO: List<ExamDTO>)
    fun generateReminders(examDataDTO: List<ExamDTO>)
}