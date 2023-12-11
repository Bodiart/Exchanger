package com.bodiart.exchanger.feature.exchanger.domain.model

data class CurrenciesEntity(
    val baseCurrency: String,
    val rates: List<CurrencyRateEntity>
)