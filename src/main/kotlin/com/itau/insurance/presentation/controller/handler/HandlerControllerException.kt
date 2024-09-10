package com.itau.insurance.presentation.controller.handler

import com.itau.insurance.application.exceptions.DataBaseGenericException
import com.itau.insurance.domain.exception.ProductNotActiveException
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

    @ExceptionHandler(ProductNotActiveException::class)
    fun productNotFound(exception: ProductNotActiveException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.create(
            message = exception.message!!,
            details = mapOf("id" to exception.id)
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(DataBaseGenericException::class)
    fun dataBaseGenericException(exception: DataBaseGenericException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.create(
            message = exception.message!!,
            details = emptyMap()
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
            message = "Error in Request",
            details = mapOf("error" to exception.message)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}
