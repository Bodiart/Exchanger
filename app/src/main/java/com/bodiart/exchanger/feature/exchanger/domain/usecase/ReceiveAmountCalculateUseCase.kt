package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import java.math.BigDecimal
import java.math.MathContext

private const val BASE_CURRENCY_RATE = 1
private const val BIG_DECIMAL_PRECISION = 6

internal class ReceiveAmountCalculateUseCaseImpl : ReceiveAmountCalculateUseCase {
    override fun invoke(
        sellCurrency: String,
        receiveCurrency: String,
        sellAmount: BigDecimal,
        currencies: CurrenciesEntity
    ): BigDecimal {
        val sellRate = findCurrencyRate(sellCurrency, currencies)
        val receiveRate = findCurrencyRate(receiveCurrency, currencies)

        val mc = MathContext(BIG_DECIMAL_PRECISION)
        return receiveRate.divide(sellRate, mc).multiply(sellAmount, mc)
    }

    // can be separate use case
    private fun findCurrencyRate(targetCurrency: String, currencies: CurrenciesEntity): BigDecimal {
        return if (targetCurrency == currencies.baseCurrency) {
            BigDecimal(BASE_CURRENCY_RATE)
        } else {
            currencies.rates
                .find { it.name == targetCurrency }
                ?.rate
                ?: throw RuntimeException("No rate for selected currency $targetCurrency")
        }
    }
}

interface ReceiveAmountCalculateUseCase {
    fun invoke(
        sellCurrency: String,
        receiveCurrency: String,
        sellAmount: BigDecimal,
        currencies: CurrenciesEntity
    ): BigDecimal
}