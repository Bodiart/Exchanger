package com.bodiart.exchanger.feature.exchanger.domain.model

import java.math.BigDecimal

/**
 * Represents exchanger actual values, full precision amounts and etc...
 */
data class ExchangerDataEntity(
    val baseCurrency: String,
    val sellAmount: BigDecimal,
    val sellCurrency: String,
    val receiveAmount: BigDecimal,
    val receiveCurrency: String,
    val availableCurrencies: List<String>
)