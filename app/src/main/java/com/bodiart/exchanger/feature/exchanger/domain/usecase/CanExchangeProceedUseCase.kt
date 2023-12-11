package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity
import java.math.BigDecimal

private const val ZERO_BALANCE = 0

internal class CanExchangeProceedUseCaseImpl(
    private val exchangeFeeGetUseCase: ExchangeFeeGetUseCase
) : CanExchangeProceedUseCase {
    override fun invoke(
        exchangerData: ExchangerDataEntity,
        balance: BalanceEntity
    ): Boolean {
        val zeroAmount = BigDecimal(0)

        if (exchangerData.sellAmount == zeroAmount || exchangerData.receiveAmount == zeroAmount) {
            return false
        }

        val sellSelectedCurrencyBalance = balance.currenciesBalance
            .find { it.currency == exchangerData.sellCurrency }
            ?.balance
            ?: BigDecimal(ZERO_BALANCE)

        val totalSellAmount = exchangerData.sellAmount +
                (exchangeFeeGetUseCase.invoke(exchangerData.sellAmount) ?: BigDecimal(0))

        return totalSellAmount <= sellSelectedCurrencyBalance
    }
}

interface CanExchangeProceedUseCase {
    fun invoke(
        exchangerData: ExchangerDataEntity,
        balance: BalanceEntity
    ): Boolean
}