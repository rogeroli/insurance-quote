package com.itau.insurance.application

import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.gateway.CatalogServiceGateway
import com.itau.insurance.domain.service.QuotationValidator
import com.itau.insurance.infrastructure.mapper.CustomerMapper
import com.itau.insurance.infrastructure.mapper.QuotationMapper
import com.itau.insurance.infrastructure.persistence.repository.CustomerRepository
import com.itau.insurance.infrastructure.persistence.repository.QuotationRepository
import org.springframework.stereotype.Service

@Service
class InsuranceQuoteService(
    private val catalogServiceGateway: CatalogServiceGateway,
    private val quotationRepository: QuotationRepository,
    private val quotationValidator: QuotationValidator
) {
    private val quotationMapper = QuotationMapper
    private val customerMapper = CustomerMapper

    fun create(quotation: Quotation) {
        quotationValidator.validateTotalCoverageAmount(quotation)

        val product = catalogServiceGateway.getProduct(quotation.productId)
        quotationValidator.validateProduct(product!!)

        val offer = catalogServiceGateway.getOffer(quotation.offerId)
        quotationValidator.validateOffer(offer!!)
        quotationValidator.validateCoverages(currentCoverage = quotation.coverages, offerCoverage = offer.coverages )
        quotationValidator.validateAssistances(currentAssistances = quotation.assistances, offerAssistances = offer.assistances)
        quotationValidator.validateTotalMonthlyPremiumAmount(
            currentValue = quotation.totalMonthlyPremiumAmount,
            minimumValue = offer.monthlyPremiumAmount.minAmount,
            maximumValue = offer.monthlyPremiumAmount.maxAmount
        )

        val quotationEntity = QuotationMapper.toEntity(quotation)
        quotationRepository.save(quotationEntity)
    }

}