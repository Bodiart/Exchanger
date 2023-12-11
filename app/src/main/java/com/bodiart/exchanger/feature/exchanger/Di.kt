@file:Suppress("MemberVisibilityCanBePrivate")

package com.bodiart.exchanger.feature.exchanger

import com.bodiart.exchanger.BuildConfig
import com.bodiart.exchanger.feature.exchanger.data.api.CurrenciesApiService
import com.bodiart.exchanger.feature.exchanger.data.dataSource.BalanceDataSource
import com.bodiart.exchanger.feature.exchanger.data.dataSource.BalanceInMemoryDataSource
import com.bodiart.exchanger.feature.exchanger.data.dataSource.CurrenciesDataSource
import com.bodiart.exchanger.feature.exchanger.data.dataSource.CurrenciesRemoteDataSource
import com.bodiart.exchanger.feature.exchanger.data.dataSource.ExchangeInfoDataSource
import com.bodiart.exchanger.feature.exchanger.data.dataSource.ExchangeInfoInMemoryDataSource
import com.bodiart.exchanger.feature.exchanger.data.repository.BalanceRepositoryImpl
import com.bodiart.exchanger.feature.exchanger.data.repository.CurrenciesRepositoryImpl
import com.bodiart.exchanger.feature.exchanger.data.repository.ExchangeFeeRepositoryImpl
import com.bodiart.exchanger.feature.exchanger.domain.BalanceRepository
import com.bodiart.exchanger.feature.exchanger.domain.CurrenciesRepository
import com.bodiart.exchanger.feature.exchanger.domain.ExchangeFeeRepository
import com.bodiart.exchanger.feature.exchanger.domain.usecase.BalanceGetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.BalanceGetUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.BalanceSetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.BalanceSetUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CanExchangeProceedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CanExchangeProceedUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrenciesGetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrenciesGetUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrencySelectedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrencySelectedUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeFeeGetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeFeeGetUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeProceedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeProceedUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeSucceedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeSucceedUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataCurrenciesUpdateUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataCurrenciesUpdateUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataInitializeUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataInitializeUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ReceiveAmountCalculateUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ReceiveAmountCalculateUseCaseImpl
import com.bodiart.exchanger.feature.exchanger.presentation.ExchangerViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// imitation of di
// (just the easiest way, haven't implemented right pattern with passing params to 'inject' functions)
object Di {
    fun baseUrl(): String {
        return BuildConfig.BASE_URL
    }

    fun currenciesUrl(): String {
        return BuildConfig.CURRENCIES_URL
    }

    // api
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    private val exchangerRetrofit = Retrofit.Builder()
        .client(okHttpClient())
        .baseUrl(baseUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun retrofit() = exchangerRetrofit

    // data

    fun currenciesApiService(): CurrenciesApiService =
        retrofit().create(CurrenciesApiService::class.java)

    // singleton
    private val balanceDataSource = BalanceInMemoryDataSource()
    fun balanceDataSource(): BalanceDataSource = balanceDataSource

    // singleton
    private val exchangeInfoDataSource = ExchangeInfoInMemoryDataSource()
    fun exchangeInfoDataSource(): ExchangeInfoDataSource = exchangeInfoDataSource

    fun currenciesDataSource(): CurrenciesDataSource =
        CurrenciesRemoteDataSource(currenciesApiService(), currenciesUrl())

    fun balanceRepository(): BalanceRepository = BalanceRepositoryImpl(balanceDataSource())

    fun currenciesRepository(): CurrenciesRepository =
        CurrenciesRepositoryImpl(currenciesDataSource())

    fun exchangeFeeRepository(): ExchangeFeeRepository =
        ExchangeFeeRepositoryImpl(exchangeInfoDataSource())

    // domain

    fun currenciesGetUseCase(): CurrenciesGetUseCase =
        CurrenciesGetUseCaseImpl(currenciesRepository())

    fun exchangerDataInitializeUseCase(): ExchangerDataInitializeUseCase =
        ExchangerDataInitializeUseCaseImpl()

    fun exchangerDataCurrenciesUpdateUseCase(): ExchangerDataCurrenciesUpdateUseCase =
        ExchangerDataCurrenciesUpdateUseCaseImpl(exchangerDataInitializeUseCase())

    fun currencySelectedUseCase(): CurrencySelectedUseCase = CurrencySelectedUseCaseImpl()

    fun exchangeFeeGetUseCase(): ExchangeFeeGetUseCase =
        ExchangeFeeGetUseCaseImpl(exchangeFeeRepository())

    fun canExchangeProceedUseCase(): CanExchangeProceedUseCase =
        CanExchangeProceedUseCaseImpl(exchangeFeeGetUseCase())

    fun balanceSetUseCase(): BalanceSetUseCase = BalanceSetUseCaseImpl(balanceRepository())

    fun exchangeSucceedUseCase(): ExchangeSucceedUseCase =
        ExchangeSucceedUseCaseImpl(exchangeFeeRepository())

    fun exchangeProceedUseCase(): ExchangeProceedUseCase =
        ExchangeProceedUseCaseImpl(
            canExchangeProceedUseCase(),
            balanceSetUseCase(),
            exchangeFeeGetUseCase(),
            exchangeSucceedUseCase()
        )

    fun balanceGetUseCase(): BalanceGetUseCase = BalanceGetUseCaseImpl(balanceRepository())

    fun receiveAmountCalculateUseCase(): ReceiveAmountCalculateUseCase =
        ReceiveAmountCalculateUseCaseImpl()

    // presentation
    val viewModel = ExchangerViewModel(
        currenciesGetUseCase = currenciesGetUseCase(),
        exchangerDataInitializeUseCase = exchangerDataInitializeUseCase(),
        exchangerDataCurrenciesUpdateUseCase = exchangerDataCurrenciesUpdateUseCase(),
        currencySelectedUseCase = currencySelectedUseCase(),
        exchangeProceedUseCase = exchangeProceedUseCase(),
        canExchangeProceedUseCase = canExchangeProceedUseCase(),
        balanceGetUseCase = balanceGetUseCase(),
        receiveAmountCalculateUseCase = receiveAmountCalculateUseCase()
    )
    fun viewModel() = viewModel
}