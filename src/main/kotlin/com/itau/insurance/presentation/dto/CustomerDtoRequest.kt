package com.itau.insurance.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.itau.insurance.domain.Customer
import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import java.util.*

data class CustomerDtoRequest(
    @JsonProperty("document_number")
    val documentNumber: String,

    val name: String,

    val type: CustomerType,

    val gender: GenderType,

    @JsonProperty("date_of_birth")
    val dateOfBirth: Date,

    val email: String,

    val phoneNumber: Long
){
    companion object {
        fun fromDomain(customer: Customer): CustomerDtoRequest {
            return customer.run {
                CustomerDtoRequest(
                    documentNumber = documentNumber,
                    name = name,
                    type = type,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    email = email,
                    phoneNumber = phoneNumber
                )
            }
        }
    }

    fun toDomain(): Customer {
        return Customer(
            documentNumber = this.documentNumber,
            name = this.name,
            type = this.type,
            gender = this.gender,
            dateOfBirth = this.dateOfBirth,
            email = this.email,
            phoneNumber = this.phoneNumber
        )
    }
}