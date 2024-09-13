package com.itau.insurance.application

import com.itau.insurance.common.Logger
import com.itau.insurance.application.exceptions.DataBaseGenericException
import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.exception.NotFoundException
import com.itau.insurance.domain.gateway.CatalogServiceGateway
import com.itau.insurance.domain.service.QuotationValidator
import com.itau.insurance.infrastructure.mapper.QuotationMapper
import com.itau.insurance.infrastructure.persistence.repository.QuotationRepository
import com.itau.insurance.infrastructure.service.publisher.QuoteReceiverPublisher
import org.hibernate.HibernateException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime

@Service
class InsuranceQuoteService(
    private val catalogServiceGateway: CatalogServiceGateway,
    private val quotationRepository: QuotationRepository,
    private val quotationValidator: QuotationValidator,
    private val quoteReceiverPublisher: QuoteReceiverPublisher) {

    private val logger = Logger.getLogger(this::class.java)
    private val mapper = QuotationMapper()

    fun create(quotation: Quotation) {
        quotationValidator.validateTotalCoverageAmount(quotation)

        val product = try{
            catalogServiceGateway.getProduct(quotation.productId)
        }catch (exception: WebClientResponseException){
            throw NotFoundException("Product with id ${quotation.productId} not found")
        }
        quotationValidator.validateProduct(product!!)

        val offer = try{
            catalogServiceGateway.getOffer(quotation.offerId)
        }catch (exception: WebClientResponseException){
            throw NotFoundException("Offer with id ${quotation.offerId} not found")
        }

        quotationValidator.validateOffer(offer!!)
        quotationValidator.validateCoverages(currentCoverage = quotation.coverages, offerCoverage = offer.coverages )
        quotationValidator.validateAssistances(currentAssistances = quotation.assistances, offerAssistances = offer.assistances)
        quotationValidator.validateTotalMonthlyPremiumAmount(
            currentValue = quotation.totalMonthlyPremiumAmount,
            minimumValue = offer.monthlyPremiumAmount.minAmount,
            maximumValue = offer.monthlyPremiumAmount.maxAmount
        )
        val quotationEntity = mapper.toEntity(quotation)

        val quotationPersisted = try{
                quotationRepository.save(quotationEntity)
        } catch (exception: HibernateException) {
            throw DataBaseGenericException("An error occurred while persisted the quotation")
        }

        logger.info("Quotation with id ${quotationPersisted.id} persisted")

        quoteReceiverPublisher.sendMessage(quotationPersisted.id!!, quotation)
    }

    fun get(id: Long): Quotation {
        val quotationOptional = quotationRepository.findById(id)

        if (quotationOptional.isEmpty) {
            throw NotFoundException("Quotation with ID $id not found")
        }

        return mapper.toDomain(quotationOptional.get())
    }

    fun setPolicy(quotationId: Long, policyId: Long) {
        try {
            val quotationOptional = quotationRepository.findById(quotationId)

            if (quotationOptional.isEmpty) {
                throw NotFoundException("Quotation with ID $quotationId not found")
            }

            val quotation = quotationOptional.get()
            quotation.policyId = policyId
            quotation.updateAt = LocalDateTime.now()

            quotationRepository.save(quotation)
            logger.info("Set policy with id ${quotation.policyId} in quotation id ${quotation.id}")
        } catch (exception: HibernateException) {
            throw DataBaseGenericException("An error occurred while updating the quotation")
        }
    }


}