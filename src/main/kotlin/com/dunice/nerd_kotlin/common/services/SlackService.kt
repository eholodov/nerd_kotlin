package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDTO

interface SlackService {

    public fun processRequest(examDataDTO: List<ExamDTO>)

}