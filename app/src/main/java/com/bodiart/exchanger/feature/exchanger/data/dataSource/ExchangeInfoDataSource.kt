package com.bodiart.exchanger.feature.exchanger.data.dataSource

internal class ExchangeInfoInMemoryDataSource : ExchangeInfoDataSource {
    private var exchangesCounter = 0

    override fun getExchangesCount(): Int {
        return exchangesCounter
    }

    override fun exchangeSucceed() {
        exchangesCounter++
    }
}

interface ExchangeInfoDataSource {
    fun getExchangesCount(): Int

    fun exchangeSucceed()
}