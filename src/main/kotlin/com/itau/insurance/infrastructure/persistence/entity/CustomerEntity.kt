package com.itau.insurance.infrastructure.persistence.entity

import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
    val documentNumber: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: CustomerType,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: GenderType,

    @Column(name = "date_of_birth", nullable = false)
    @Temporal(TemporalType.DATE)
    val dateOfBirth: Date,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: Long,

    @OneToMany(mappedBy = "customer")
    val quotations: List<QuotationEntity> = emptyList()
)
