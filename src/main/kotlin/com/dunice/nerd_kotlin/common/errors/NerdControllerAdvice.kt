package com.dunice.nerd_kotlin.common.errors

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException


@ControllerAdvice
class NerdControllerAdvice {

    @ExceptionHandler(value = [ConstraintViolationException::class, ValueInstantiationException::class,
        HttpMessageNotReadableException::class, MethodArgumentNotValidException::class])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    private fun handleValidationException(ex: Exception): String {
//        println("Validation Exception is: ${ex.message}")


//        return when (ex) {
    //            is ConstraintViolationException -> print((ex as ConstraintViolationException).message)
//            is ConstraintViolationException -> "test"
//            is ValueInstantiationException -> "ValueInstantiationException"
//            is HttpMessageNotReadableException -> "HTTP"
//            is MethodArgumentNotValidException -> "MethodArgumentNotValidException"
//            else -> "Unknown Exception"
//        }

        return "Validation Exception is: ${ex.stackTraceToString()}"
    }

//    @ExceptionHandler(CustomException::class)
//    fun handleCustomException(ex: CustomException) {
//        println("🤢 Custom Exception is: ${ex.message} 🤢")
//    }
//
//    @ExceptionHandler(SlackEmailNotFoundException::class)
//    private fun handleEmailNotFoundException(ex: SlackEmailNotFoundException) {
//        println("🤢 EmailNotFound Exception is: ${ex.message} 🤢")
//    }
//
//    @ExceptionHandler(MalformedJwtException::class)
//    private fun handeWrongTokenException(exception: MalformedJwtException) {
//        println("🤢MalformedJwtException is : Unable to read JSON value🤢")
//    }
}