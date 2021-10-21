package com.dunice.nerd_kotlin.common.errors

import com.dunice.nerd_kotlin.common.services.slack.SlackService
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class NerdControllerAdvice (val slackService: SlackService) {

    fun generateResponse(message: String) : ResponseEntity<String> = ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(value = [ConstraintViolationException::class, ValueInstantiationException::class,
        HttpMessageNotReadableException::class, MethodArgumentNotValidException::class])
    fun handleValidationException(ex: Exception) : ResponseEntity<String>{
        slackService.sendLogMessage("Validation Exception is: ${ex.message}")
        return generateResponse("Validation Exception is: ${ex.message}")
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException) : ResponseEntity<String>{
        slackService.sendLogMessage("Custom Exception is: ${ex.message}")
        return generateResponse("Custom Exception is: ${ex.message}")
    }

    @ExceptionHandler(SlackEmailNotFoundException::class)
    fun handleEmailNotFoundException(ex: SlackEmailNotFoundException) : ResponseEntity<String>{
        slackService.sendLogMessage("EmailNotFound Exception is: ${ex.message}")
        return generateResponse("EmailNotFound Exception is: ${ex.message}")
    }

    @ExceptionHandler(MalformedJwtException::class)
    fun handleWrongTokenException(exception: MalformedJwtException) : ResponseEntity<String>{
        slackService.sendLogMessage("MalformedJwtException is : Unable to read JSON value")
        return generateResponse("MalformedJwtException is : Unable to read JSON value")
    }

    @ExceptionHandler(Exception::class)
    fun handleCommonException(exception: Exception) : ResponseEntity<String> {
        slackService.sendLogMessage("Other exception is: ${exception.message}")
        return generateResponse("Other exception is: ${exception.message}")
    }
}