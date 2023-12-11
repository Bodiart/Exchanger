package com.bodiart.exchanger.feature.exchanger.data.repository

import com.bodiart.exchanger.feature.exchanger.data.dataSource.ExchangeInfoDataSource
import com.bodiart.exchanger.feature.exchanger.domain.ExchangeFeeRepository
import java.math.BigDecimal

private const val FREE_FEE_EXCHANGES_COUNT = 5
private const val FEE_PERCENT = 0.007

internal class ExchangeFeeRepositoryImpl(
    private val exchangeInfoDataSource: ExchangeInfoDataSource
) : ExchangeFeeRepository {
    override fun shouldTakeFee(): Boolean {
        return exchangeInfoDataSource.getExchangesCount() >= FREE_FEE_EXCHANGES_COUNT
    }

    override fun getFee(currencyAmount: BigDecimal): BigDecimal {
        return currencyAmount * BigDecimal(FEE_PERCENT)
    }

    override fun exchangeSucceed() {
        exchangeInfoDataSource.exchangeSucceed()
    }
}