package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrencyBalanceEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangeProceedResultEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity
import java.math.BigDecimal

internal class ExchangeProceedUseCaseImpl(
    private val canExchangeProceedUseCase: CanExchangeProceedUseCase,
    private val balanceSetUseCase: BalanceSetUseCase,
    private val exchangeFeeGetUseCase: ExchangeFeeGetUseCase,
    private val exchangeSucceedUseCase: ExchangeSucceedUseCase
) : ExchangeProceedUseCase {
    override suspend fun invoke(
        exchangerData: ExchangerDataEntity,
        balance: BalanceEntity
    ): ExchangeProceedResultEntity {
        if (!canExchangeProceedUseCase.invoke(exchangerData, balance)) {
            return ExchangeProceedResultEntity.Failure("Not enough balance")
        }

        // next logic usually implemented by back-end side, so its not very beautiful
        // it just working (to not spend to much time)
        // cycles can be optimized

        val zeroBalance = BigDecimal(0)
        val fee = exchangeFeeGetUseCase.invoke(exchangerData.sellAmount)

        val newSellCurrencyBalance = balance.currenciesBalance
            .find { it.currency == exchangerData.sellCurrency }
            ?.balance
            ?.let { it - exchangerData.sellAmount - (fee ?: BigDecimal(0)) }
            ?: return ExchangeProceedResultEntity.Failure("Balance cant be found")

        val newReceiveCurrencyBalance = balance.currenciesBalance
            .find { it.currency == exchangerData.receiveCurrency }
            ?.balance
            .let { (it ?: zeroBalance) + exchangerData.receiveAmount }


        val newBalance = balance.copy(
            // can be optimized
            currenciesBalance = balance.currenciesBalance.toMutableList().apply {
                // change sell balance
                find { it.currency == exchangerData.sellCurrency }
                    ?.let { remove(it) }
                add(CurrencyBalanceEntity(exchangerData.sellCurrency, newSellCurrencyBalance))
                // change buy balance
                find { it.currency == exchangerData.receiveCurrency }
                    ?.let { remove(it) }
                add(CurrencyBalanceEntity(exchangerData.receiveCurrency, newReceiveCurrencyBalance))
            }.filter { it.balance > zeroBalance }
        )
        balanceSetUseCase.invoke(newBalance)
        // update exchanges counter
        exchangeSucceedUseCase.invoke()

        return ExchangeProceedResultEntity.Success(
            sellAmount = exchangerData.sellAmount,
            sellCurrency = exchangerData.sellCurrency,
            receiveAmount = exchangerData.receiveAmount,
            receiveCurrency = exchangerData.receiveCurrency,
            fee = fee
        )
    }
}

interface ExchangeProceedUseCase {
    suspend fun invoke(
        exchangerData: ExchangerDataEntity,
        balance: BalanceEntity
    ): ExchangeProceedResultEntity
}