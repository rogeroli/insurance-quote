package com.itau.insurance.domain.gateway

import com.itau.insurance.infrastructure.gateway.dto.response.OfferDtoResponse
import com.itau.insurance.infrastructure.gateway.dto.response.ProductDtoResponse
import java.util.UUID

interface CatalogServiceGateway {

    fun getProduct(productId: UUID): ProductDtoResponse?

    fun getOffer(offerId: UUID): OfferDtoResponse?
}