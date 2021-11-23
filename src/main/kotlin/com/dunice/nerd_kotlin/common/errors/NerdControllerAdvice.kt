package com.dunice.nerd_kotlin.common.errors

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class NerdControllerAdvice {

    @ExceptionHandler(value = [ConstraintViolationException::class, ValueInstantiationException::class,
        HttpMessageNotReadableException::class, MethodArgumentNotValidException::class])
    private fun handleValidationException(ex: Exception) {
        println("🤢 Validation Exception is: ${ex.message} 🤢")
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException) {
        println("🤢 Custom Exception is: ${ex.message} 🤢")
    }

    @ExceptionHandler(SlackEmailNotFoundException::class)
    private fun handleEmailNotFoundException(ex: SlackEmailNotFoundException) {
        println("🤢 EmailNotFound Exception is: ${ex.message} 🤢")
    }

    @ExceptionHandler(MalformedJwtException::class)
    private fun handeWrongTokenException(exception: MalformedJwtException) {
        println("🤢MalformedJwtException is : Unable to read JSON value🤢")
    }
}