package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.CurrencyOperationEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity

internal class CurrencySelectedUseCaseImpl : CurrencySelectedUseCase {
    override fun invoke(
        exchangerData: ExchangerDataEntity,
        currency: String,
        operation: CurrencyOperationEntity
    ): ExchangerDataEntity {
        var sellSelectedCurrency = exchangerData.sellCurrency
        var receiveSelectedCurrency = exchangerData.receiveCurrency

        when (operation) {
            CurrencyOperationEntity.SELL -> {
                // replace currencies if user selected receive currency
                if (currency == exchangerData.receiveCurrency) {
                    receiveSelectedCurrency = sellSelectedCurrency
                    sellSelectedCurrency = currency
                } else {
                    sellSelectedCurrency = currency
                }
            }
            CurrencyOperationEntity.RECEIVE -> {
                // replace currencies if user selected sell currency
                if (currency == exchangerData.sellCurrency) {
                    sellSelectedCurrency = receiveSelectedCurrency
                    receiveSelectedCurrency = currency
                } else {
                    receiveSelectedCurrency = currency
                }
            }
        }

        return exchangerData.copy(
            sellCurrency = sellSelectedCurrency,
            receiveCurrency = receiveSelectedCurrency
        )
    }
}

interface CurrencySelectedUseCase {
    fun invoke(
        exchangerData: ExchangerDataEntity,
        currency: String,
        operation: CurrencyOperationEntity
    ): ExchangerDataEntity
}