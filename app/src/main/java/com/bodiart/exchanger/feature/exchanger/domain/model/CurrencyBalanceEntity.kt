package com.bodiart.exchanger.feature.exchanger.domain.model

import java.math.BigDecimal

data class CurrencyBalanceEntity(
    val currency: String,
    val balance: BigDecimal
)