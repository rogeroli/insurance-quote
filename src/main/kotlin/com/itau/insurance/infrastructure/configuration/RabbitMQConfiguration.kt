package com.itau.insurance.infrastructure.configuration

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration(
    @Value("\${rabbitmq.exchange}") private val exchange: String,
    @Value("\${rabbitmq.routingkey.insurance-policy-create}") private val insurancePolicyCreateRoutingKey: String,
    @Value("\${rabbitmq.routingkey.insurance-quote-received}") private val insuranceQuoteReceivedRoutingKey: String,
    @Value("\${rabbitmq.queue.insurance-policy-create}") private val insurancePolicyCreateQueue: String,
    @Value("\${rabbitmq.queue.insurance-quote-received}") private val insuranceQuoteReceivedQueue: String,
    @Value("\${rabbitmq.queue.insurance-policy-create-dlq}") private val insurancePolicyCreateDlq: String,
    @Value("\${rabbitmq.ttl}") private val ttl: Long,
    @Value("\${rabbitmq.max-redeliveries}") private val maxRedeliveries: Int
){

    @Bean
    fun createInsurancePolicyCreateQueue(): Queue {
        return Queue(insurancePolicyCreateQueue, true)
    }

    @Bean
    fun createInsuranceQuoteReceivedQueue(): Queue {
        return Queue(insuranceQuoteReceivedQueue, true)
    }

    @Bean
    fun createInsurancePolicyCreateDlq(): Queue {
        val args = mapOf(
            "x-message-ttl" to ttl,
            "x-max-length" to maxRedeliveries
        )
        return Queue(insurancePolicyCreateDlq, true, false, false, args)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(exchange)
    }

    @Bean
    fun bindingInsurancePolicyCreateQueue(
        exchange: TopicExchange,
        @Qualifier("createInsurancePolicyCreateQueue") queue: Queue
    ): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(insurancePolicyCreateRoutingKey)
    }

    @Bean
    fun bindingInsuranceQuoteReceivedQueue(
        exchange: TopicExchange,
        @Qualifier("createInsuranceQuoteReceivedQueue") queue: Queue
    ): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(insuranceQuoteReceivedRoutingKey)
    }

    @Bean
    fun bindingInsurancePolicyCreateDlq(
        exchange: TopicExchange,
        @Qualifier("createInsurancePolicyCreateDlq") dlq: Queue
    ): Binding {
        return BindingBuilder.bind(dlq).to(exchange).with(insurancePolicyCreateRoutingKey)
    }
}