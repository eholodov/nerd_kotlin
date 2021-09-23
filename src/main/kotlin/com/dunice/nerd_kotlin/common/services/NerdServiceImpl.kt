package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import org.springframework.stereotype.Service

@Service
class NerdServiceImpl (val slackService: SlackService) : NerdService {

    override fun getDataFromRiseUp(examDataDTO: ExamDataDTO) {
        println("🤢${examDataDTO}🤢")
        //slackService.sendMessage(examDataDTO)
    }
}

//{
//    datetime: "2021-09-22T07:00:00.000Z",
//    nameStudent: "John Smith",
//    subject: "JS",
//    room: "2Floor",
//    interviewer: "Ivan Galtovich",
//}