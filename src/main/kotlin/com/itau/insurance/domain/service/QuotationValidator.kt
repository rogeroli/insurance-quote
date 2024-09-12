package com.itau.insurance.domain.service

import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.exception.*
import com.itau.insurance.infrastructure.gateway.dto.response.OfferDtoResponse
import com.itau.insurance.infrastructure.gateway.dto.response.ProductDtoResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class QuotationValidator {

    fun validateProduct(product: ProductDtoResponse) {
        if (!product.active) {
            throw ValidationDataException("Product with id ${product.id} is disable")
        }
    }

    fun validateOffer(offer: OfferDtoResponse) {
        if (!offer.active) {
            throw ValidationDataException("Offer with id ${offer.id} is disable")
        }
    }

    fun validateCoverages(currentCoverage: Map<String, BigDecimal>, offerCoverage: Map<String, BigDecimal>) {
        val allKeysPresent = currentCoverage.keys.all { it in offerCoverage.keys }

        if (!allKeysPresent) {
            throw ValidationDataException("Coverages not present")
        }

        currentCoverage.forEach { (coverage, currentValue) ->
            val offerValue = offerCoverage[coverage]
                ?: throw ValidationDataException("Coverage $coverage not found in offer")

            if (currentValue >= offerValue) {
                throw  throw ValidationDataException("Coverage amount $coverage is not below the permitted amount")
            }
        }
    }

    fun validateAssistances(currentAssistances: List<String>, offerAssistances: List<String>) {
        val allAssistancesPresent = currentAssistances.all { it in offerAssistances }

        if (!allAssistancesPresent) {
            throw ValidationDataException("Not all assistance is present in the offers")
        }
    }

    fun validateTotalMonthlyPremiumAmount(
        currentValue: BigDecimal, minimumValue: BigDecimal, maximumValue: BigDecimal
    ) {
        if (currentValue < minimumValue || currentValue > maximumValue) {
            throw ValidationDataException(
                "Total monthly premium value ($currentValue) is outside the allowed range"
            )
        }
    }

    fun validateTotalCoverageAmount(quotation: Quotation) {
        val calculatedTotalCoverage = quotation.coverages.values.reduce { acc, coverage -> acc + coverage }

        if (calculatedTotalCoverage != quotation.totalCoverageAmount) {
            throw ValidationDataException("The total coverage amount (${calculatedTotalCoverage}) does not match " +
                    "the amount reported (${quotation.totalCoverageAmount})"
            )
        }
    }
}
