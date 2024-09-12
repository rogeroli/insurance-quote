package com.itau.insurance.infrastructure.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.util.Date

@Entity
@Table(name = "customer")
data class CustomerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "document_number", nullable = false)
    val documentNumber: String = "",

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: CustomerType = CustomerType.NATURAL,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: GenderType = GenderType.FEMALE,

    @Column(name = "date_of_birth", nullable = false)
    @Temporal(TemporalType.DATE)
    val dateOfBirth: Date = Date(),

    @Column(name = "email", nullable = false)
    val email: String = "",

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: Long = 0L,

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    val quotations: List<QuotationEntity> = emptyList()
) {
    constructor() : this(
        id = null,
        documentNumber = "",
        name = "",
        type = CustomerType.NATURAL,
        gender = GenderType.FEMALE,
        dateOfBirth = Date(),
        email = "",
        phoneNumber = 0L,
        quotations = emptyList()
    )
}
