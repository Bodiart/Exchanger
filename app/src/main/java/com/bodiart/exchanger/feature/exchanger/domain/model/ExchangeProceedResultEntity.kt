package com.bodiart.exchanger.feature.exchanger.domain.model

import java.math.BigDecimal

sealed class ExchangeProceedResultEntity {
    data class Success(
        val sellAmount: BigDecimal,
        val sellCurrency: String,
        val receiveAmount: BigDecimal,
        val receiveCurrency: String,
        val fee: BigDecimal?
    ) : ExchangeProceedResultEntity()

    data class Failure(val reason: String) : ExchangeProceedResultEntity()
}