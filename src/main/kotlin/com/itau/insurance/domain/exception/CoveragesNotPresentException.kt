package com.itau.insurance.domain.exception

class CoveragesNotPresentException(message: String = "Cobertura não encontrada na oferta."): RuntimeException(message)
