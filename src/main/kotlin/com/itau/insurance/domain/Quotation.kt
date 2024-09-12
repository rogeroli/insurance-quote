package com.itau.insurance.domain

import com.itau.insurance.domain.enums.CategoryType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Quotation (
    val id: Long?=null,
    val policyId: Long?=null,
    val productId: UUID,
    val offerId: UUID,
    val category: CategoryType,
    val totalMonthlyPremiumAmount: BigDecimal,
    val totalCoverageAmount: BigDecimal,
    val coverages: Map<String, BigDecimal>,
    val assistances: List<String>,
    val customer: Customer,
    val createdAt: LocalDateTime?=null,
    val updatedAt: LocalDateTime?=null
)
