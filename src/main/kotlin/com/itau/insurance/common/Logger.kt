package com.itau.insurance.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {
    fun getLogger(type: Class<*>): Logger = LoggerFactory.getLogger(type)
}
