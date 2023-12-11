package com.bodiart.exchanger.feature.exchanger.data.dataSource

import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrencyBalanceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal

private const val DEFAULT_CURRENCY_NAME = "EUR"
private const val DEFAULT_CURRENCY_BALANCE = 1000

internal class BalanceInMemoryDataSource : BalanceDataSource {
    private val balanceMutableFlow = MutableStateFlow(
        BalanceEntity(getInitialBalance())
    )

    override fun getBalance(): Flow<BalanceEntity> {
        return balanceMutableFlow
    }

    override suspend fun setBalance(balance: BalanceEntity) {
        balanceMutableFlow.emit(balance)
    }

    private fun getInitialBalance() = listOf(
        CurrencyBalanceEntity(
            currency = DEFAULT_CURRENCY_NAME,
            balance = BigDecimal(DEFAULT_CURRENCY_BALANCE)
        )
    )
}

interface BalanceDataSource {
    fun getBalance(): Flow<BalanceEntity>

    suspend fun setBalance(balance: BalanceEntity)
}