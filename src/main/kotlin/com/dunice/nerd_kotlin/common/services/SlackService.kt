package com.dunice.nerd_kotlin.common.services

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse

class SlackService {
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
}