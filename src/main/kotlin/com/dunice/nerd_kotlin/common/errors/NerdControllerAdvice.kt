package com.dunice.nerd_kotlin.common.errors

import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.jsonwebtoken.MalformedJwtException
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
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): String {
        return "Custom Exception is: ${ex.message}"
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(SlackEmailNotFoundException::class)
    private fun handleEmailNotFoundException(ex: SlackEmailNotFoundException): String {
        return "EmailNotFound Exception is: ${ex.message} "
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MalformedJwtException::class)
    private fun handeWrongTokenException(exception: MalformedJwtException): String {
        
        return "MalformedJwtException is : Unable to read JSON value"
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): String? {
        return "Runtime ${ex.message}"
    }


}