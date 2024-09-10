package com.itau.insurance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.validation.annotation.Validated

@SpringBootApplication
class InsuranceApplication

fun main(args: Array<String>) {
	runApplication<InsuranceApplication>(*args)
}
