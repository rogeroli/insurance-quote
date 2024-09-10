package com.itau.insurance.domain.exception

import java.util.UUID

class OfferNotActiveException(val id: UUID, message: String = "Oferta não ativa") : RuntimeException(message)
