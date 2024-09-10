package com.itau.insurance.infrastructure.mapper

import com.itau.insurance.domain.Customer
import com.itau.insurance.infrastructure.persistence.entity.CustomerEntity

object CustomerMapper {

    fun toEntity(customer: Customer): CustomerEntity {
        return CustomerEntity(
            documentNumber = customer.documentNumber,
            name = customer.name,
            type = customer.type,
            gender = customer.gender,
            dateOfBirth = customer.dateOfBirth,
            email = customer.email,
            phoneNumber = customer.phoneNumber
        )
    }

    fun toDomain(customerEntity: CustomerEntity): Customer {
        return Customer(
            documentNumber = customerEntity.documentNumber,
            name = customerEntity.name,
            type = customerEntity.type,
            gender = customerEntity.gender,
            dateOfBirth = customerEntity.dateOfBirth,
            email = customerEntity.email,
            phoneNumber = customerEntity.phoneNumber
        )
    }
}
