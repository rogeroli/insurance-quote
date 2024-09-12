package com.itau.insurance.infrastructure.service.dto.messages

import com.itau.insurance.domain.Quotation

data class QuotationMessage(
    val quotationId: Long,
    val quotation: Quotation
)
