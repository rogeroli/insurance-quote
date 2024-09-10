package com.itau.insurance.domain

import com.itau.insurance.domain.enums.CategoryType
import java.math.BigDecimal
import java.util.*

data class Quotation (
    val productId: UUID,
    val offerId: UUID,
    val category: CategoryType,
    val totalMonthlyPremiumAmount: BigDecimal,
    val totalCoverageAmount: BigDecimal,
    val coverages: Map<String, BigDecimal>,
    val assistances: List<String>,
    val customer: Customer
)
