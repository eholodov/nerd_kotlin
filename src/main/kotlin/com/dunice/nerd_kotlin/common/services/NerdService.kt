package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDataDTO

interface NerdService {

    public fun getDataFromRiseUp(examDataDTO: ExamDataDTO)
}