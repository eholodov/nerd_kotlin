package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDTO

interface NerdService {

    public fun getDataFromRiseUp(examDTO: List<ExamDTO>)
}