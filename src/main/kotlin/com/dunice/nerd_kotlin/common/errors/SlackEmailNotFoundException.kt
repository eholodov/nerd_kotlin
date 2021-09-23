package com.dunice.nerd_kotlin.common.errors

class SlackEmailNotFoundException(val missingEmail: String) : RuntimeException() {

    override val message = "${missingEmail} not found in Slack!"

}