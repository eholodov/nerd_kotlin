package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDTO
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val slackService: SlackService) : NerdService {

    override fun getDataFromRiseUp(examDTO: ExamDTO) {
        slackService.sendMessage(examDTO)
    }
}

