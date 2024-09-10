package com.itau.insurance.domain.exception

class TotalMonthlyPremiumAmountException(
    message: String = "Valor total do prêmio mensal está fora do intervalo permitido."): RuntimeException(message)