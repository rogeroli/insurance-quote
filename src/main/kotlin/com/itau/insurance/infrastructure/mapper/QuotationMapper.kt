package com.itau.insurance.infrastructure.mapper

import com.itau.insurance.domain.Quotation
import com.itau.insurance.infrastructure.persistence.entity.QuotationEntity

class QuotationMapper {

    fun toEntity(quotation: Quotation): QuotationEntity {
        return QuotationEntity(
            productId = quotation.productId,
            offerId = quotation.offerId,
            category = quotation.category,
            totalMonthlyPremiumAmount = quotation.totalMonthlyPremiumAmount,
            totalCoverageAmount = quotation.totalCoverageAmount,
            coverages = quotation.coverages,
            assistances = quotation.assistances,
            customer = CustomerMapper.toEntity(quotation.customer)
        )
    }

    fun toDomain(quotationEntity: QuotationEntity): Quotation {
        return Quotation(
            id = quotationEntity.id,
            policyId = quotationEntity.policyId,
            productId = quotationEntity.productId,
            offerId = quotationEntity.offerId,
            category = quotationEntity.category,
            totalMonthlyPremiumAmount = quotationEntity.totalMonthlyPremiumAmount,
            totalCoverageAmount = quotationEntity.totalCoverageAmount,
            coverages = quotationEntity.coverages,
            assistances = quotationEntity.assistances,
            customer = CustomerMapper.toDomain(quotationEntity.customer),
            createdAt = quotationEntity.createAt,
            updatedAt = quotationEntity.updateAt
        )
    }
}
