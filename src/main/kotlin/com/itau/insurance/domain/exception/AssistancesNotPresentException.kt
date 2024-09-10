package com.itau.insurance.domain.exception

class AssistancesNotPresentException(message: String = "Nem todas as assistências estão presentes nas ofertas."):
    RuntimeException(message)