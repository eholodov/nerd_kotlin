package com.dunice.nerd_kotlin.common.errors

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class NerdControllerAdvice {

    //UCP7QJVD5

//    @ExceptionHandler(Exception::class)
//    fun handleException(ex: Exception) {
//        println("🤢 Exception is: ${ex.message} 🤢")
//    }

    @ExceptionHandler(value = [ConstraintViolationException::class, ValueInstantiationException::class,
        HttpMessageNotReadableException::class, MethodArgumentNotValidException::class])
    private fun handleValidationException(ex: Exception) {
        println("🤢 Validation Exception is: ${ex.message} 🤢")
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException) {
        println("🤢 Custom Exception is: ${ex.message} 🤢")
    }

}