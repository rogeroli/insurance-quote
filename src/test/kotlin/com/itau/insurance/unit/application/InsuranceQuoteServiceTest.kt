package com.itau.insurance.unit.application

import com.itau.insurance.application.InsuranceQuoteService
import com.itau.insurance.common.Logger
import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.exception.NotFoundException
import com.itau.insurance.domain.gateway.CatalogServiceGateway
import com.itau.insurance.domain.service.QuotationValidator
import com.itau.insurance.infrastructure.mapper.QuotationMapper
import com.itau.insurance.infrastructure.persistence.repository.QuotationRepository
import com.itau.insurance.infrastructure.service.publisher.QuoteReceiverPublisher
import com.itau.insurance.application.exceptions.DataBaseGenericException
import com.itau.insurance.domain.Customer
import com.itau.insurance.domain.enums.CategoryType
import com.itau.insurance.domain.enums.CustomerType
import com.itau.insurance.domain.enums.GenderType
import com.itau.insurance.infrastructure.gateway.dto.response.MonthlyPremiumAmount
import com.itau.insurance.infrastructure.gateway.dto.response.OfferDtoResponse
import com.itau.insurance.infrastructure.gateway.dto.response.ProductDtoResponse
import com.itau.insurance.infrastructure.mapper.CustomerMapper
import com.itau.insurance.infrastructure.persistence.entity.QuotationEntity
import io.mockk.*
import org.hibernate.HibernateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
class InsuranceQuoteServiceTest {

    private lateinit var insuranceQuoteService: InsuranceQuoteService
    private val catalogServiceGateway = mockk<CatalogServiceGateway>()
    private val quotationRepository = mockk<QuotationRepository>()
    private val quotationValidator = mockk<QuotationValidator>()
    private val quoteReceiverPublisher = mockk<QuoteReceiverPublisher>()
    private val mapper = mockk<QuotationMapper>()

    @BeforeEach
    fun setup() {
        insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway,
            quotationRepository,
            quotationValidator,
            quoteReceiverPublisher
        )
    }

    @Test
    fun `should throw NotFoundException when product is not found`() {
        val quotation = mockk<Quotation>()
        val productId = UUID.randomUUID()
        every { quotation.productId } returns productId

        val errorBody = "Error: Product not found"
        val errorBodyBytes = errorBody.toByteArray(StandardCharsets.UTF_8)

        val webClientResponseException = WebClientResponseException(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            HttpHeaders.EMPTY,
            errorBodyBytes,
            StandardCharsets.UTF_8
        )

        val quotationValidator = mockk<QuotationValidator>()
        every { quotationValidator.validateTotalCoverageAmount(quotation) } just Runs

        val catalogServiceGateway = mockk<CatalogServiceGateway>()
        every { catalogServiceGateway.getProduct(productId) } throws webClientResponseException

        val insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway = catalogServiceGateway,
            quotationRepository = mockk(),
            quotationValidator = quotationValidator,
            quoteReceiverPublisher = mockk()
        )

        val exception = assertFailsWith<NotFoundException> {
            insuranceQuoteService.create(quotation)
        }

        assertEquals("Product with id $productId not found", exception.message)
    }

    @Test
    fun `should throw NotFoundException when offer is not found`() {
        val quotation = mockk<Quotation>()
        val productId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        every { quotation.productId } returns productId
        every { quotation.offerId } returns offerId

        val catalogServiceGateway = mockk<CatalogServiceGateway>()
        every { catalogServiceGateway.getProduct(productId) } returns mockk()
        val errorBody = "Error: Offer not found"
        val errorBodyBytes = errorBody.toByteArray(StandardCharsets.UTF_8)
        val webClientResponseException = WebClientResponseException(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            HttpHeaders.EMPTY,
            errorBodyBytes,
            StandardCharsets.UTF_8
        )
        every { catalogServiceGateway.getOffer(offerId) } throws webClientResponseException

        val quotationValidator = mockk<QuotationValidator>()
        every { quotationValidator.validateTotalCoverageAmount(quotation) } just Runs
        every { quotationValidator.validateProduct(any()) } just Runs

        val insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway = catalogServiceGateway,
            quotationRepository = mockk(),
            quotationValidator = quotationValidator,
            quoteReceiverPublisher = mockk()
        )

        val exception = assertFailsWith<NotFoundException> {
            insuranceQuoteService.create(quotation)
        }

        assertEquals("Offer with id $offerId not found", exception.message)
    }

    @Test
    fun `should throw HibernateException when saving quotation fails`() {
        val productId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val coverages = mapOf("coverage1" to BigDecimal("100.00"))
        val assistances = listOf("assistance1")
        val totalMonthlyPremiumAmount = BigDecimal("50.00")
        val totalCoverageAmount = BigDecimal("200.00")

        val customer = mockk<Customer>()
        every { customer.documentNumber } returns "123456789"
        every { customer.name } returns "Teste"
        every { customer.type } returns CustomerType.NATURAL
        every { customer.gender } returns GenderType.FEMALE
        every { customer.email } returns "teste@teste.com"
        every { customer.dateOfBirth } returns Date()
        every { customer.phoneNumber } returns 1234567

        val quotationEntity = QuotationEntity(
            id = 1L,
            productId = productId,
            offerId = offerId,
            category = CategoryType.LIFE,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = CustomerMapper.toEntity(customer),
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )

        val quotation = Quotation(
            id = 1L,
            productId = productId,
            offerId = offerId,
            category = CategoryType.LIFE,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = customer,
            createdAt = quotationEntity.createAt,
            updatedAt = quotationEntity.updateAt
        )

        val quotationRepository = mockk<QuotationRepository>()
        every { quotationRepository.save(any()) } throws HibernateException("Database error")

        val catalogServiceGateway = mockk<CatalogServiceGateway>()
        val product = mockk<ProductDtoResponse>()

        val offer = mockk<OfferDtoResponse>()
        every { offer.coverages } returns coverages
        every { offer.assistances } returns assistances
        every { offer.monthlyPremiumAmount.minAmount } returns totalMonthlyPremiumAmount
        every { offer.monthlyPremiumAmount.maxAmount } returns totalMonthlyPremiumAmount

        every { catalogServiceGateway.getProduct(productId) } returns product
        every { catalogServiceGateway.getOffer(offerId) } returns offer

        val quotationValidator = mockk<QuotationValidator>()
        every { quotationValidator.validateTotalCoverageAmount(quotation) } just Runs
        every { quotationValidator.validateProduct(any()) } just Runs
        every { quotationValidator.validateOffer(any()) } just Runs
        every { quotationValidator.validateCoverages(any(), any()) } just Runs
        every { quotationValidator.validateAssistances(any(), any()) } just Runs
        every { quotationValidator.validateTotalMonthlyPremiumAmount(any(), any(), any()) } just Runs

        val mapper = mockk<QuotationMapper>()
        every { mapper.toEntity(any()) } returns mockk<QuotationEntity>()

        val quoteReceiverPublisher = mockk<QuoteReceiverPublisher>()

        val insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway = catalogServiceGateway,
            quotationRepository = quotationRepository,
            quotationValidator = quotationValidator,
            quoteReceiverPublisher = quoteReceiverPublisher
        )

        val exception = assertFailsWith<DataBaseGenericException> {
            insuranceQuoteService.create(quotation)
        }

        assertEquals("An error occurred while persisted the quotation", exception.message)

        verify { quotationRepository.save(any()) }
    }

    @Test
    fun `should call save and publish message when creating quotation succeeds`() {
        val id = 1L
        val productId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val category = CategoryType.LIFE
        val totalMonthlyPremiumAmount = BigDecimal("50.00")
        val totalCoverageAmount = BigDecimal("200.00")
        val coverages = mapOf("coverage1" to BigDecimal("100.00"))
        val assistances = listOf("assistance1")

        val customer = mockk<Customer>()
        every { customer.documentNumber } returns "123456789"
        every { customer.name } returns "Teste"
        every { customer.type } returns CustomerType.NATURAL
        every { customer.gender } returns GenderType.FEMALE
        every { customer.email } returns "teste@teste.com"
        every { customer.dateOfBirth } returns Date()
        every { customer.phoneNumber } returns 1234567

        val quotationEntity = QuotationEntity(
            id = id,
            productId = productId,
            offerId = offerId,
            category = category,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = CustomerMapper.toEntity(customer),
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )

        val quotation = Quotation(
            id = id,
            productId = productId,
            offerId = offerId,
            category = category,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = customer,
            createdAt = quotationEntity.createAt,
            updatedAt = quotationEntity.updateAt
        )

        val quotationRepository = mockk<QuotationRepository>()
        every { quotationRepository.save(any()) } returns quotationEntity

        val catalogServiceGateway = mockk<CatalogServiceGateway>()
        val product = mockk<ProductDtoResponse>()

        val offer = mockk<OfferDtoResponse>()
        every { offer.coverages } returns coverages
        every { offer.assistances } returns assistances
        every { offer.monthlyPremiumAmount.minAmount } returns totalMonthlyPremiumAmount
        every { offer.monthlyPremiumAmount.maxAmount } returns totalMonthlyPremiumAmount

        every { catalogServiceGateway.getProduct(productId) } returns product
        every { catalogServiceGateway.getOffer(offerId) } returns offer

        val quotationValidator = mockk<QuotationValidator>()
        every { quotationValidator.validateTotalCoverageAmount(quotation) } just Runs
        every { quotationValidator.validateProduct(any()) } just Runs
        every { quotationValidator.validateOffer(any()) } just Runs
        every { quotationValidator.validateCoverages(any(), any()) } just Runs
        every { quotationValidator.validateAssistances(any(), any()) } just Runs
        every { quotationValidator.validateTotalMonthlyPremiumAmount(any(), any(), any()) } just Runs

        val quoteReceiverPublisher = mockk<QuoteReceiverPublisher>()
        every { quoteReceiverPublisher.sendMessage(any(), any()) } just Runs

        val mapper = mockk<QuotationMapper>()
        every { mapper.toEntity(any()) } returns mockk<QuotationEntity>()

        val insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway = catalogServiceGateway,
            quotationRepository = quotationRepository,
            quotationValidator = quotationValidator,
            quoteReceiverPublisher = quoteReceiverPublisher
        )

        insuranceQuoteService.create(quotation)

        verify { quotationRepository.save(any()) }
        verify { quoteReceiverPublisher.sendMessage(any(), any()) }
    }

    @Test
    fun `should throw NotFoundException when getting a quotation by id`() {
        val id = 1L
        every { quotationRepository.findById(id) } returns Optional.empty()

        val exception = assertFailsWith<NotFoundException> {
            insuranceQuoteService.get(id)
        }

        assertEquals("Quotation with ID $id not found", exception.message)
    }

    @Test
    fun `should return a quotation when getting by id`() {
        val id = 1L
        val productId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val category = CategoryType.LIFE
        val totalMonthlyPremiumAmount = BigDecimal("50.00")
        val totalCoverageAmount = BigDecimal("200.00")
        val coverages = mapOf("coverage1" to BigDecimal("100.00"))
        val assistances = listOf("assistance1")

        val customer = mockk<Customer>()
        every { customer.documentNumber } returns "123456789"
        every { customer.name } returns "Teste"
        every { customer.type } returns CustomerType.NATURAL
        every { customer.gender } returns GenderType.FEMALE
        every { customer.email } returns "teste@teste.com"
        every { customer.dateOfBirth } returns Date()
        every { customer.phoneNumber } returns 1234567

        val quotationEntity = QuotationEntity(
            id = id,
            productId = productId,
            offerId = offerId,
            category = category,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = CustomerMapper.toEntity(customer),
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )

        val quotation = Quotation(
            id = id,
            productId = productId,
            offerId = offerId,
            category = category,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = customer,
            createdAt = quotationEntity.createAt,
            updatedAt = quotationEntity.updateAt
        )

        every { quotationRepository.findById(id) } returns Optional.of(quotationEntity)

        val mapper = mockk<QuotationMapper>()
        every { mapper.toDomain(quotationEntity) } returns quotation

        val insuranceQuoteService = InsuranceQuoteService(
            catalogServiceGateway = mockk(),
            quotationRepository = quotationRepository,
            quotationValidator = mockk(),
            quoteReceiverPublisher = mockk()
        )

        val result = insuranceQuoteService.get(id)

        assertEquals(quotation.id, result.id)
        assertEquals(quotation.productId, result.productId)
        assertEquals(quotation.offerId, result.offerId)
        assertEquals(quotation.category, result.category)
        assertEquals(quotation.createdAt, result.createdAt)
        assertEquals(quotation.updatedAt, result.updatedAt)
        assertEquals(quotation.policyId, result.policyId)
        assertEquals(quotation.totalCoverageAmount, result.totalCoverageAmount)
    }

    @Test
    fun `should set policy when quotation is found`() {
        val quotationId = 1L
        val policyId = 2L
        val existingQuotationEntity = QuotationEntity(
            id = quotationId,
            policyId = null,
            productId = UUID.randomUUID(),
            offerId = UUID.randomUUID(),
            category = CategoryType.LIFE,
            totalMonthlyPremiumAmount = BigDecimal("100.00"),
            totalCoverageAmount = BigDecimal("200.00"),
            coverages = emptyMap(),
            assistances = emptyList(),
            customer = mockk(),
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )

        every { quotationRepository.findById(quotationId) } returns Optional.of(existingQuotationEntity)
        every { quotationRepository.save(any()) } returns existingQuotationEntity

        insuranceQuoteService.setPolicy(quotationId, policyId)

        assertEquals(policyId, existingQuotationEntity.policyId)
        assertNotNull(existingQuotationEntity.updateAt)

        verify { quotationRepository.save(existingQuotationEntity) }
    }

    @Test
    fun `should throw NotFoundException when quotation is not found`() {
        val quotationId = 1L
        val policyId = 2L

        every { quotationRepository.findById(quotationId) } returns Optional.empty()

        val exception = assertThrows<NotFoundException> {
            insuranceQuoteService.setPolicy(quotationId, policyId)
        }

        assertEquals("Quotation with ID $quotationId not found", exception.message)
    }

    @Test
    fun `should throw DataBaseGenericException when there is a database error`() {
        val id = 1L
        val productId = UUID.randomUUID()
        val offerId = UUID.randomUUID()
        val category = CategoryType.LIFE
        val totalMonthlyPremiumAmount = BigDecimal("50.00")
        val totalCoverageAmount = BigDecimal("200.00")
        val coverages = mapOf("coverage1" to BigDecimal("100.00"))
        val assistances = listOf("assistance1")

        val customer = mockk<Customer>()
        every { customer.documentNumber } returns "123456789"
        every { customer.name } returns "Teste"
        every { customer.type } returns CustomerType.NATURAL
        every { customer.gender } returns GenderType.FEMALE
        every { customer.email } returns "teste@teste.com"
        every { customer.dateOfBirth } returns Date()
        every { customer.phoneNumber } returns 1234567

        val quotationEntity = QuotationEntity(
            id = id,
            productId = productId,
            offerId = offerId,
            category = category,
            totalMonthlyPremiumAmount = totalMonthlyPremiumAmount,
            totalCoverageAmount = totalCoverageAmount,
            coverages = coverages,
            assistances = assistances,
            customer = CustomerMapper.toEntity(customer),
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )

        every { quotationRepository.findById(id) } returns Optional.of(quotationEntity)
        every { quotationRepository.save(any()) } throws HibernateException("Database error")

        val exception = assertThrows<DataBaseGenericException> {
            insuranceQuoteService.setPolicy(id, 2L)
        }

        assertEquals("An error occurred while updating the quotation", exception.message)
    }

}
