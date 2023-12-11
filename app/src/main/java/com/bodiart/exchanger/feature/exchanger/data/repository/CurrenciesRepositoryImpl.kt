package com.bodiart.exchanger.feature.exchanger.data.repository

import com.bodiart.exchanger.feature.exchanger.data.dataSource.CurrenciesDataSource
import com.bodiart.exchanger.feature.exchanger.domain.CurrenciesRepository
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrencyRateEntity

class CurrenciesRepositoryImpl(
    private val currenciesDataSource: CurrenciesDataSource
) : CurrenciesRepository {
    override suspend fun getCurrencies(): CurrenciesEntity {
        val currencies = currenciesDataSource.getCurrencies()
        return CurrenciesEntity(
            currencies.base,
            rates = currencies.rates.map {
                CurrencyRateEntity(
                    name = it.key,
                    rate = it.value
                )
            }
        )
    }
}