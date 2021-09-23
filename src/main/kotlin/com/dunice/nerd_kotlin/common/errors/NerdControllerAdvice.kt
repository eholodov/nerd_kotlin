package com.dunice.nerd_kotlin.common.errors

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class NerdControllerAdvice {

    @ExceptionHandler(SlackEmailNotFoundException::class)
    private fun handleEmailNotFoundException(ex: SlackEmailNotFoundException) {
        println("🤢 EmailNotFound Exception is: ${ex.message} 🤢")
    }
}