package com.itau.insurance.presentation.controller.handler

import com.itau.insurance.application.exceptions.DataBaseGenericException
import com.itau.insurance.domain.exception.NotFoundException
import com.itau.insurance.domain.exception.ValidationDataException
import com.itau.insurance.presentation.dto.ErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class HandlerControllerException : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    fun notFound(exception: NotFoundException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.create(
            message = exception.message!!,
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(ValidationDataException::class)
    fun validationDataException(exception: ValidationDataException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.create(
            message = exception.message!!,
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(DataBaseGenericException::class)
    fun dataBaseGenericException(exception: DataBaseGenericException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.create(
            message = exception.message!!,
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    override fun handleHttpMessageNotReadable(
        exception: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val response = ErrorResponse.create(
            message = "Error in Request"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}
