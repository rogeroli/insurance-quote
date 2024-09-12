package com.itau.insurance.presentation.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val message: String,
) {
    companion object {
        fun create(message: String): ErrorResponse {
            return ErrorResponse(message)
        }
    }
}