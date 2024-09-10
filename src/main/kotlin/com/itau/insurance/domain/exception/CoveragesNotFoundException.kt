package com.itau.insurance.domain.exception

class CoveragesNotFoundException(
    message: String = "Nem todas as coberturas estão presentes na oferta."): RuntimeException(message)
