package com.itau.insurance.presentation.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val message: String,
    val details: Map<String, Any?>
) {
    companion object {
        fun create(message: String, details: Map<String, Any?>): ErrorResponse {
            return ErrorResponse(message, details)
        }
    }
}