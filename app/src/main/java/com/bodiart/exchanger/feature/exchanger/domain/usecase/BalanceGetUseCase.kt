package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.BalanceRepository
import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import kotlinx.coroutines.flow.Flow

internal class BalanceGetUseCaseImpl(
    private val balanceRepository: BalanceRepository
) : BalanceGetUseCase {
    override fun invoke(): Flow<BalanceEntity> {
        return balanceRepository.getBalance()
    }
}

interface BalanceGetUseCase {
    fun invoke(): Flow<BalanceEntity>
}