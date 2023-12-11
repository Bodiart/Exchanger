package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.ExchangeFeeRepository
import java.math.BigDecimal

internal class ExchangeFeeGetUseCaseImpl(
    private val exchangeFeeRepository: ExchangeFeeRepository
) : ExchangeFeeGetUseCase {
    override fun invoke(currencyAmount: BigDecimal): BigDecimal? {
        return if (exchangeFeeRepository.shouldTakeFee()) {
            exchangeFeeRepository.getFee(currencyAmount)
        } else {
            null
        }
    }
}

interface ExchangeFeeGetUseCase {
    /**
     * @return fee part of sell currency amount; null means that no fee should be taken
     */
    fun invoke(currencyAmount: BigDecimal): BigDecimal?
}