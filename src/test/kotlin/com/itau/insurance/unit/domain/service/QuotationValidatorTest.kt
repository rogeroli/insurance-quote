package com.itau.insurance.unit.domain.service

import com.itau.insurance.domain.Quotation
import com.itau.insurance.domain.enums.CategoryType
import com.itau.insurance.domain.exception.ValidationDataException
import com.itau.insurance.domain.service.QuotationValidator
import com.itau.insurance.infrastructure.gateway.dto.response.MonthlyPremiumAmount
import com.itau.insurance.infrastructure.gateway.dto.response.OfferDtoResponse
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.test.BeforeTest

@SpringBootTest
@ActiveProfiles("test")
class QuotationValidatorTest {

    private lateinit var quotationValidator: QuotationValidator

    @BeforeTest
    fun setup() {
        quotationValidator = QuotationValidator()
    }

    @Test
    fun `should throw ValidationDataException when product is inactive`() {
        val offerDtoResponse = OfferDtoResponse(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            name = "Product 1",
            createdAt = LocalDateTime.now(),
            active = false,
            coverages = emptyMap(),
            assistances = emptyList(),
            monthlyPremiumAmount = MonthlyPremiumAmount(
                maxAmount = BigDecimal(500),
                minAmount = BigDecimal(100),
                suggestedAmount = BigDecimal(200)
            )
        )
        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateOffer(offerDtoResponse)
        }

        assert(exception.message == "Offer with id ${offerDtoResponse.id} is disable")
    }

    @Test
    fun `should not throw exception when product is active`() {
        val offerDtoResponse = OfferDtoResponse(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            name = "Product 1",
            createdAt = LocalDateTime.now(),
            active = true,
            coverages = mapOf("COVERAGE_A" to BigDecimal(100)),
            assistances = listOf("ASSISTANCE_A"),
            monthlyPremiumAmount = MonthlyPremiumAmount(
                maxAmount = BigDecimal(500),
                minAmount = BigDecimal(100),
                suggestedAmount = BigDecimal(200)
            )
        )

        quotationValidator.validateOffer(offerDtoResponse)
    }

    @Test
    fun `should throw ValidationDataException when coverage is not present in the offer`() {
        val currentCoverage = mapOf("COVERAGE_A" to BigDecimal(100))
        val offerCoverage = mapOf("COVERAGE_B" to BigDecimal(200))

        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateCoverages(currentCoverage, offerCoverage)
        }

        assert(exception.message == "Coverages not present")
    }

    @Test
    fun `should throw ValidationDataException when coverage amount is not below the permitted amount`() {
        val currentCoverage = mapOf("COVERAGE_A" to BigDecimal(300))
        val offerCoverage = mapOf("COVERAGE_A" to BigDecimal(200))

        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateCoverages(currentCoverage, offerCoverage)
        }

        assert(exception.message == "Coverage amount COVERAGE_A is not below the permitted amount")
    }

    @Test
    fun `should throw ValidationDataException when assistance is not present in the offer`() {
        val currentAssistances = listOf("ASSISTANCE_A")
        val offerAssistances = listOf("ASSISTANCE_B")

        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateAssistances(currentAssistances, offerAssistances)
        }

        assert(exception.message == "Not all assistance is present in the offers")
    }

    @Test
    fun `should throw ValidationDataException when total monthly premium value is outside the allowed range`() {
        val offerDtoResponse = OfferDtoResponse(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            name = "Product 1",
            createdAt = LocalDateTime.now(),
            active = true,
            coverages = mapOf("COVERAGE_A" to BigDecimal(100)),
            assistances = listOf("ASSISTANCE_A"),
            monthlyPremiumAmount = MonthlyPremiumAmount(
                maxAmount = BigDecimal(120),
                minAmount = BigDecimal(100),
                suggestedAmount = BigDecimal(110)
            )
        )

        val currentValue = BigDecimal(150)

        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateTotalMonthlyPremiumAmount(currentValue, offerDtoResponse.monthlyPremiumAmount.minAmount, offerDtoResponse.monthlyPremiumAmount.maxAmount)
        }

        assert(exception.message == "Total monthly premium value ($currentValue) is outside the allowed range")
    }

    @Test
    fun `should throw ValidationDataException when total coverage amount does not match`() {
        val quotation = Quotation(
            id = 1L,
            policyId = 12345L,
            productId = UUID.randomUUID(),
            offerId = UUID.randomUUID(),
            category = CategoryType.LIFE,
            totalMonthlyPremiumAmount = BigDecimal(200),
            totalCoverageAmount = BigDecimal(100),
            coverages = mapOf("COVERAGE_A" to BigDecimal(200), "COVERAGE_B" to BigDecimal(100)),
            assistances = listOf("ASSISTANCE_A"),
            customer = mockk(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val exception = assertThrows<ValidationDataException> {
            quotationValidator.validateTotalCoverageAmount(quotation)
        }

        assert(exception.message == "The total coverage amount (300) does not match the amount reported (100)")
    }
}
