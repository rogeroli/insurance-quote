package com.itau.insurance.domain.exception

class CoveragesValueException(
    message: String = "O valor da cobertura não está abaixo do permitido."): RuntimeException(message)
