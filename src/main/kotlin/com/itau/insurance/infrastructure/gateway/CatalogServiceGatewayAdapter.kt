package com.itau.insurance.infrastructure.gateway

import com.itau.insurance.domain.gateway.CatalogServiceGateway
import com.itau.insurance.infrastructure.gateway.dto.response.OfferDtoResponse
import com.itau.insurance.infrastructure.gateway.dto.response.ProductDtoResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class CatalogServiceGatewayAdapter(
    @Value("\${gateway.catalog-service.url}")
    private val baseUrl: String,
    private val webClient: WebClient
): CatalogServiceGateway {

    override fun getProduct(productId: UUID): ProductDtoResponse? {
        return webClient.get()
            .uri("$baseUrl/product/{productId}", productId)
            .retrieve()
            .bodyToMono(ProductDtoResponse::class.java)
            .block()
    }

    override fun getOffer(offerId: UUID): OfferDtoResponse? {
        return webClient.get()
            .uri("$baseUrl/offer/{offerId}", offerId)
            .retrieve()
            .bodyToMono(OfferDtoResponse::class.java)
            .block()
    }
}