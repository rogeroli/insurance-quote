package com.itau.insurance.domain.exception

class TotalCoverageException(
    message: String = "O valor total das coberturas não corresponde ao informado."): RuntimeException(message)

