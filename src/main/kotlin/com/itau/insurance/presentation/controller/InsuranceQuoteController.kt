package com.itau.insurance.presentation.controller

import com.itau.insurance.application.InsuranceQuoteService
import com.itau.insurance.presentation.dto.QuotationDtoRequest
import com.itau.insurance.presentation.dto.QuotationDtoResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<QuotationDtoResponse> {
        val quotation = service.get(id)
        return ResponseEntity.ok(QuotationDtoResponse.fromDomain(quotation))
    }
}