package com.bodiart.exchanger.feature.exchanger.data.api

import com.bodiart.exchanger.feature.exchanger.data.model.CurrenciesDto
import retrofit2.http.GET
import retrofit2.http.Url

interface CurrenciesApiService {
    @GET
    suspend fun getCurrencies(@Url url: String): CurrenciesDto
}