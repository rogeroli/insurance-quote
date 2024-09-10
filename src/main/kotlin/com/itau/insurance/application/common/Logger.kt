package com.itau.insurance.application.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {
    fun getLogger(type: Class<*>): Logger = LoggerFactory.getLogger(type)
}
