package com.itau.insurance.infrastructure.gateway.dto.response

import java.time.LocalDateTime
import java.util.*

data class ProductDtoResponse(
    val id: UUID,
    val name: String,
    val createdAt: LocalDateTime,
    val active: Boolean,
    val offers: List<UUID>
)