package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDataDTO

interface SlackService {

    public fun sendMessage(examDataDTO: ExamDataDTO)

}