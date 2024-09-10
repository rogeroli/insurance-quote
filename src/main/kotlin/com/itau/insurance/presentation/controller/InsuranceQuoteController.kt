package com.itau.insurance.presentation.controller

import com.itau.insurance.application.InsuranceQuoteService
import com.itau.insurance.presentation.dto.QuotationDtoRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/insurance/quote")
class InsuranceQuoteController(
    @Autowired val service: InsuranceQuoteService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody quotationDtoRequest: QuotationDtoRequest) {
        service.create(quotationDtoRequest.toDomain())
    }
}