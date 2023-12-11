package com.bodiart.exchanger.feature.exchanger.domain.model

import java.math.BigDecimal

data class CurrencyRateEntity(
    val name: String,
    val rate: BigDecimal
)