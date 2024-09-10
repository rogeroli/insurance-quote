package com.itau.insurance.domain.exception

import java.util.UUID

class ProductNotActiveException(val id: UUID, message: String = "Produto não ativo") : RuntimeException(message)
