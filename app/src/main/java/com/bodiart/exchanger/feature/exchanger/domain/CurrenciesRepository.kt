package com.bodiart.exchanger.feature.exchanger.domain

import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity

interface CurrenciesRepository {
    suspend fun getCurrencies(): CurrenciesEntity
}