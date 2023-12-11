package com.bodiart.exchanger.feature.exchanger.data.model

import java.math.BigDecimal

data class CurrenciesDto(
    val base: String,
    val rates: Map<String, BigDecimal>
)