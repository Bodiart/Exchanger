package com.bodiart.exchanger.feature.exchanger.data.dataSource

import com.bodiart.exchanger.feature.exchanger.data.api.CurrenciesApiService
import com.bodiart.exchanger.feature.exchanger.data.model.CurrenciesDto

internal class CurrenciesRemoteDataSource(
    private val currenciesApiService: CurrenciesApiService,
    private val currenciesUrl: String
) : CurrenciesDataSource {
    override suspend fun getCurrencies(): CurrenciesDto {
        return currenciesApiService.getCurrencies(currenciesUrl)
    }
}

interface CurrenciesDataSource {
    suspend fun getCurrencies(): CurrenciesDto
}