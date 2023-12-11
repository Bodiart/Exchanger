package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity
import com.bodiart.exchanger.feature.exchanger.util.DEFAULT_AMOUNT
import java.math.BigDecimal

internal class ExchangerDataInitializeUseCaseImpl : ExchangerDataInitializeUseCase {
    override fun invoke(currencies: CurrenciesEntity): ExchangerDataEntity {
        return ExchangerDataEntity(
            baseCurrency = currencies.baseCurrency,
            sellAmount = BigDecimal(DEFAULT_AMOUNT),
            sellCurrency = currencies.baseCurrency,
            receiveAmount = BigDecimal(DEFAULT_AMOUNT),
            receiveCurrency = currencies.rates.firstOrNull()?.name ?: "",
            availableCurrencies = currencies.rates.map { it.name } + currencies.baseCurrency,
        )
    }
}

interface ExchangerDataInitializeUseCase {
    fun invoke(currencies: CurrenciesEntity): ExchangerDataEntity
}