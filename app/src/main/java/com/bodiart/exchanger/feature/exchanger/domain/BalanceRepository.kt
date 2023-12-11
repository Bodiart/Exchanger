package com.bodiart.exchanger.feature.exchanger.domain

import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    fun getBalance(): Flow<BalanceEntity>

    suspend fun setBalance(balance: BalanceEntity)
}