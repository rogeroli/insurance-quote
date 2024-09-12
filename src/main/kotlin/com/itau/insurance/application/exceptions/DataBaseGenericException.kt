package com.itau.insurance.application.exceptions

import com.itau.insurance.common.Logger

class DataBaseGenericException(message: String = "Database error") : RuntimeException(message) {
    companion object {
        private val logger = Logger.getLogger(this::class.java)
    }

    init {
        logger.error("[DATABASE_ERROR] $message")
    }
}
