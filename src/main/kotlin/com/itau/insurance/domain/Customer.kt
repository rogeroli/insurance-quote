package com.itau.insurance.domain

import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import java.util.*

data class Customer(
    val documentNumber: String,
    val name: String,
    val type: CustomerType,
    val gender: GenderType,
    val dateOfBirth: Date,
    val email: String,
    val phoneNumber: Long
)

