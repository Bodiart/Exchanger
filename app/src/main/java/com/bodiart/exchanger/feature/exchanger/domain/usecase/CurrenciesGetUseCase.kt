package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.CurrenciesRepository
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

private const val CURRENCIES_GET_PERIOD_MILLIS = 5000L

/**
 * Right now this process will be active even when app is on bg.
 * There are 2 best options to fix it:
 * 1) subscribe this flow from ui with lifecycle
 * 2) use some custom app state monitor (preferable)
 * I didn't implement it as i haven't seen this requirement.
 */
internal class CurrenciesGetUseCaseImpl(
    private val currenciesRepository: CurrenciesRepository
) : CurrenciesGetUseCase {
    override fun invoke(): Flow<CurrenciesEntity> {
        return flow {
            while (currentCoroutineContext().isActive) {
                // if some error - just skip it, we don need to handle errors
                runCatching { currenciesRepository.getCurrencies() }.getOrNull()?.let {
                    emit(it)
                }
                delay(CURRENCIES_GET_PERIOD_MILLIS)
            }
        }
    }
}

interface CurrenciesGetUseCase {
    fun invoke(): Flow<CurrenciesEntity>
}