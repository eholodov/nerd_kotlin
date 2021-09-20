package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.utils.getNumberOfWeek
import com.dunice.nerd_kotlin.common.utils.groupCardsByInterviewerAndAssistant
import com.dunice.nerd_kotlin.common.utils.groupCardsByPerson
import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import org.springframework.stereotype.Service

@Service
class SlackService (val googleService: GoogleService) {
    var messageText : String = "Hello"

    var slack : Slack = Slack.getInstance()

    var token : String = ""

    var channelId : String = ""

    fun postMessage() {
        var methods = slack.methods(token)
        var request = ChatPostMessageRequest.builder()
            .channel(channelId)
            .text(messageText)
            .build();
        var chatPostMessageResponse : ChatPostMessageResponse = methods.chatPostMessage(request)
    }

    fun validateFilterGroupUserList(
    ) {
        val sheetData = googleService.getInformation(getNumberOfWeek())
        var employees = groupCardsByPerson(sheetData).keys + groupCardsByInterviewerAndAssistant(sheetData).keys

    }
}