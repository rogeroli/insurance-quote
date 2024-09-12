package com.itau.insurance.infrastructure.service.publisher

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itau.insurance.application.common.Logger
import com.itau.insurance.domain.Quotation
import com.itau.insurance.infrastructure.service.dto.messages.QuotationMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class QuoteReceiverPublisher(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.exchange}") private val exchange: String,
    @Value("\${rabbitmq.routingkey.insurance-quote-received}") private val insuranceQuoteReceivedRoutingKey: String
) {
    private val objectMapper = jacksonObjectMapper()
    private val logger = Logger.getLogger(this::class.java)

    fun sendMessage(idQuotation: Long, quotation: Quotation) {
        try{
            rabbitTemplate.convertAndSend(exchange, insuranceQuoteReceivedRoutingKey,
                objectMapper.writeValueAsString(QuotationMessage(idQuotation, quotation))

            )
            logger.info("Message insurance-quote-received published")
        }
        catch (exception: Exception){
            logger.error("Error to send insurance-quote-received message: ${exception.message}")
        }

    }
}
