package com.bodiart.exchanger.feature.exchanger.domain

import java.math.BigDecimal

interface ExchangeFeeRepository {
    fun shouldTakeFee(): Boolean

    fun getFee(currencyAmount: BigDecimal): BigDecimal

    fun exchangeSucceed()
}