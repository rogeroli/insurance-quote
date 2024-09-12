package com.itau.insurance.presentation.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/queue")
class QueueController(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.exchange}") private val exchange: String,
    @Value("\${rabbitmq.routingkey.insurance-policy-create}") private val insurancePolicyCreateRoutingKey: String,
) {

    private val objectMapper = jacksonObjectMapper()

    @PostMapping("/send")
    fun sendMessage(@RequestBody policyMessage: InsurancePolicyMessageDto): ResponseEntity<String> {
        rabbitTemplate.convertAndSend(exchange, insurancePolicyCreateRoutingKey,
            objectMapper.writeValueAsString(policyMessage)
        )

        return ResponseEntity.ok("Message sent to the queue")
    }
}

data class InsurancePolicyMessageDto(
    val quotationId: Long,
    val policyId: Long
)

