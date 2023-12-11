package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.ExchangeFeeRepository

internal class ExchangeSucceedUseCaseImpl(
    private val exchangeFeeRepository: ExchangeFeeRepository
) : ExchangeSucceedUseCase {
    override fun invoke() {
        exchangeFeeRepository.exchangeSucceed()
    }
}

interface ExchangeSucceedUseCase {
    fun invoke()
}