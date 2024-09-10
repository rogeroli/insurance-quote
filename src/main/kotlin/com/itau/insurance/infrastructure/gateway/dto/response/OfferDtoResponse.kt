package com.itau.insurance.infrastructure.gateway.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OfferDtoResponse(
    val id: UUID,

    @JsonProperty("product_id")
    val productId: UUID,

    val name: String,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime,

    val active: Boolean,

    val coverages: Map<String, BigDecimal>,

    val assistances: List<String>,

    @JsonProperty("monthly_premium_amount")
    val monthlyPremiumAmount: MonthlyPremiumAmount
)

data class MonthlyPremiumAmount(
    @JsonProperty("max_amount")
    val maxAmount: BigDecimal,

    @JsonProperty("min_amount")
    val minAmount: BigDecimal,

    @JsonProperty("suggested_amount")
    val suggestedAmount: BigDecimal
)