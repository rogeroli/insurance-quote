package com.itau.insurance.infrastructure.service.listener

import com.itau.insurance.infrastructure.service.dto.messages.InsurancePolicyMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.itau.insurance.application.InsuranceQuoteService
import com.itau.insurance.application.common.Logger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PolicyCreatedListener(
    @Autowired val insuranceQuoteService: InsuranceQuoteService
) {
    private val objectMapper = jacksonObjectMapper()
    private val logger = Logger.getLogger(this::class.java)

    @RabbitListener(queues = ["\${rabbitmq.queue.insurance-policy-create}"])
    fun receiveMessage(message: String) {
        try{
            val policyMessage: InsurancePolicyMessage = objectMapper.readValue(message)

            insuranceQuoteService.setPolicy(
                quotationId = policyMessage.quotationId,
                policyId = policyMessage.policyId
            )

            logger.info("Message insurance-policy-create received")
        }
        catch (exception: Exception){
            logger.info("Error to receive insurance-policy-create message: ${exception.message}")
        }
    }
}