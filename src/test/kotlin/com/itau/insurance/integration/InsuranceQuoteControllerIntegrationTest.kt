package com.itau.insurance.integration


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.itau.insurance.presentation.dto.QuotationDtoResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class InsuranceQuoteControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        findAndRegisterModules()
        configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should create quotation successfully`() {
        val requestJson = getRequestJson()

        mockMvc.perform(
            post("/insurance/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated)
    }

    @Test
    fun `should get quotation successfully`() {
        val requestJson = getRequestJson()

        mockMvc.perform(
            post("/insurance/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated).andReturn()

        val quotationId = 1L

        mockMvc.perform(
            get("/insurance/quote/$quotationId")
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)
            .andExpect { result ->
                val response = objectMapper.readValue(result.response.contentAsString, QuotationDtoResponse::class.java)
                assertEquals(response.productId, UUID.fromString("1b2da7cc-b367-4196-8a78-9cfeec21f586"))
                assertEquals(response.totalMonthlyPremiumAmount, BigDecimal(60.00).setScale(2))
                assertEquals(response.offerId, UUID.fromString("adc56d77-348c-4bf0-908f-22d402ee715c"))
                assertNull(response.insurancePolicyId)
                assertEquals(response.id, 1L)
       }
    }

    private fun getRequestJson(): String =
     """
            {
                "product_id": "1b2da7cc-b367-4196-8a78-9cfeec21f586",
                "offer_id": "adc56d77-348c-4bf0-908f-22d402ee715c",
                "category": "HOME",
                "total_monthly_premium_amount": 60,
                "total_coverage_amount": 980000,
                "coverages": {
                  "Incêndio": 400000,
                      "Desastres naturais": 500000,
                      "Responsabiliadade civil": 70000,
                      "Roubo": 10000
                },
                "assistances": [
                  "Encanador",
                      "Eletricista",
                      "Chaveiro 24h",
                      "Assistência Funerária"
                ],
                "customer": {
                  "document_number": "string",
                  "name": "string",
                  "type": "NATURAL",
                  "gender": "FEMALE",
                  "date_of_birth": "2024-09-09T22:03:48.236Z",
                  "email": "string",
                  "phoneNumber": 0
                }
              }
            """.trimIndent()
}

