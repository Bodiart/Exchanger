package com.bodiart.exchanger.feature.exchanger.data.repository

import com.bodiart.exchanger.feature.exchanger.data.dataSource.BalanceDataSource
import com.bodiart.exchanger.feature.exchanger.domain.BalanceRepository
import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import kotlinx.coroutines.flow.Flow

class BalanceRepositoryImpl(
    private val balanceDataSource: BalanceDataSource
) : BalanceRepository {
    override fun getBalance(): Flow<BalanceEntity> {
        return balanceDataSource.getBalance()
    }

    override suspend fun setBalance(balance: BalanceEntity) {
        balanceDataSource.setBalance(balance)
    }
}