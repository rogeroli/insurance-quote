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
            throw ProductNotActiveException(product.id)
        }
    }

    fun validateOffer(offer: OfferDtoResponse) {
        if (!offer.active) {
            throw OfferNotActiveException(offer.id)
        }
    }

    fun validateCoverages(currentCoverage: Map<String, BigDecimal>, offerCoverage: Map<String, BigDecimal>) {
        val allKeysPresent = currentCoverage.keys.all { it in offerCoverage.keys }

        if (!allKeysPresent) {
            throw CoveragesNotPresentException()
        }

        currentCoverage.forEach { (coverage, currentValue) ->
            val offerValue = offerCoverage[coverage]
                ?: throw CoveragesNotPresentException("Cobertura $coverage não encontrada na oferta.")

            if (currentValue >= offerValue) {
                throw CoveragesValueException("O valor da cobertura $coverage não está abaixo do permitido.")
            }
        }
    }

    fun validateAssistances(currentAssistances: List<String>, offerAssistances: List<String>) {
        val allAssistancesPresent = currentAssistances.all { it in offerAssistances }

        if (!allAssistancesPresent) {
            throw AssistancesNotPresentException()
        }
    }

    fun validateTotalMonthlyPremiumAmount(
        currentValue: BigDecimal, minimumValue: BigDecimal, maximumValue: BigDecimal
    ) {
        if (currentValue < minimumValue || currentValue > maximumValue) {
            throw TotalMonthlyPremiumAmountException(
                "Valor total do prêmio mensal ($currentValue) está fora do intervalo permitido."
            )
        }
    }

    fun validateTotalCoverageAmount(quotation: Quotation) {
        val calculatedTotalCoverage = quotation.coverages.values.reduce { acc, coverage -> acc + coverage }

        if (calculatedTotalCoverage != quotation.totalCoverageAmount) {
            throw TotalCoverageException("O valor total das coberturas (${calculatedTotalCoverage}) " +
                    "não corresponde ao informado (${quotation.totalCoverageAmount}).")
        }
    }
}
