package com.dunice.nerd_kotlin.matrixSchedule

import com.dunice.nerd_kotlin.common.types.ExamDTO

interface NerdService {

    fun getDataFromRiseUp(examDTO: List<ExamDTO>)
}