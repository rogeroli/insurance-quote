package com.itau.insurance.presentation.dto
import com.fasterxml.jackson.annotation.JsonProperty
import com.itau.insurance.domain.Customer
import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.enums.CategoryType
import java.math.BigDecimal
import java.util.UUID

data class QuotationDtoRequest(
    @JsonProperty("product_id")
    val productId: UUID,

    @JsonProperty("offer_id")
    val offerId: UUID,

    val category: CategoryType,

    @JsonProperty("total_monthly_premium_amount")
    val totalMonthlyPremiumAmount: BigDecimal,

    @JsonProperty("total_coverage_amount")
    val totalCoverageAmount: BigDecimal,

    val coverages: Map<String, BigDecimal>,

    val assistances: List<String>,

    val customer: CustomerDtoRequest
){
    companion object {
        fun fromDomain(quotation: Quotation): QuotationDtoRequest {
            return quotation.run {
                QuotationDtoRequest(
                    productId = productId,
                    offerId = offerId,
                    category = category,
                    totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
                    totalCoverageAmount = totalCoverageAmount,
                    coverages = coverages,
                    assistances = assistances,
                    customer = CustomerDtoRequest.fromDomain(customer)
                )
            }
        }
    }

    fun toDomain(): Quotation {
        return Quotation(
            productId = this.productId,
            offerId = this.offerId,
            category = this.category,
            totalMonthlyPremiumAmount = this.totalMonthlyPremiumAmount,
            totalCoverageAmount = this.totalCoverageAmount,
            coverages = this.coverages,
            assistances = this.assistances,
            customer = this.customer.toDomain()
        )
    }
}

