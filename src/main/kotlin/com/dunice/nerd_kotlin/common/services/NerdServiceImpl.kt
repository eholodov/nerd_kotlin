package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val slackService: SlackService) : NerdService {

    override fun getDataFromRiseUp(examDataDTO: ExamDataDTO) {
        slackService.sendMessage(examDataDTO)
    }
}