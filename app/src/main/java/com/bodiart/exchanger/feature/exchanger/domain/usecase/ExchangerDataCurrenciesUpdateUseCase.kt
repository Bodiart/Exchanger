package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity

internal class ExchangerDataCurrenciesUpdateUseCaseImpl(
    private val exchangerDataInitializeUseCase: ExchangerDataInitializeUseCase
) : ExchangerDataCurrenciesUpdateUseCase {
    override fun invoke(exchangerData: ExchangerDataEntity, currencies: CurrenciesEntity): ExchangerDataEntity {
        if (exchangerData.baseCurrency != currencies.baseCurrency) {
            return exchangerDataInitializeUseCase.invoke(currencies, )
        }

        val availableCurrencies = currencies.rates.map { it.name } + currencies.baseCurrency

        // if some of selected currencies don't exist anymore (can be optimized)
        if (
            !availableCurrencies.contains(exchangerData.sellCurrency) ||
            !availableCurrencies.contains(exchangerData.receiveCurrency)
        ) {
            return exchangerDataInitializeUseCase.invoke(currencies)
        }

        return exchangerData
    }
}

interface ExchangerDataCurrenciesUpdateUseCase {
    fun invoke(exchangerData: ExchangerDataEntity, currencies: CurrenciesEntity): ExchangerDataEntity
}