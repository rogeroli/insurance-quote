package com.itau.insurance.presentation.dto
import com.fasterxml.jackson.annotation.JsonProperty
import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.enums.CategoryType
import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class QuotationDtoResponse(
    val id: Long,
    @JsonProperty("insurance_policy_id") val insurancePolicyId: Long?,
    @JsonProperty("product_id") val productId: UUID,
    @JsonProperty("offer_id") val offerId: UUID,
    val category: CategoryType,
    @JsonProperty("created_at") val createdAt: String,
    @JsonProperty("updated_tt") val updatedAt: String,
    @JsonProperty("total_monthly_premium_amount") val totalMonthlyPremiumAmount: BigDecimal,
    @JsonProperty("total_coverage_amount")val totalCoverageAmount: BigDecimal,
    val coverages: Map<String, BigDecimal>,
    val assistances: List<String>,
    val customer: CustomerDtoResponse
){
    companion object {
        fun fromDomain(quotation: Quotation): QuotationDtoResponse {
            return QuotationDtoResponse(
                id = quotation.id!!,
                insurancePolicyId = quotation.policyId,
                productId = quotation.productId,
                offerId = quotation.offerId,
                category = quotation.category,
                createdAt = quotation.createdAt!!.toString(),
                updatedAt = quotation.updatedAt!!.toString(),
                totalMonthlyPremiumAmount = quotation.totalMonthlyPremiumAmount,
                totalCoverageAmount = quotation.totalCoverageAmount,
                coverages = quotation.coverages,
                assistances = quotation.assistances,
                customer = CustomerDtoResponse(
                    documentNumber = quotation.customer.documentNumber,
                    name = quotation.customer.name,
                    type = quotation.customer.type,
                    gender = quotation.customer.gender,
                    dateOfBirth = quotation.customer.dateOfBirth.toString(),
                    email = quotation.customer.email,
                    phoneNumber = quotation.customer.phoneNumber
                )
            )
        }
    }
}

data class CustomerDtoResponse(
    @JsonProperty("document_number") val documentNumber: String,
    val name: String,
    val type: CustomerType,
    val gender: GenderType,
    @JsonProperty("date_of_birth") val dateOfBirth: String,
    val email: String,
    @JsonProperty("phone_number") val phoneNumber: Long
)