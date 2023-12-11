package com.bodiart.exchanger.feature.exchanger.domain.usecase

import com.bodiart.exchanger.feature.exchanger.domain.BalanceRepository
import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity

internal class BalanceSetUseCaseImpl(
    private val balanceRepository: BalanceRepository
) : BalanceSetUseCase {
    override suspend fun invoke(balance: BalanceEntity) {
        balanceRepository.setBalance(balance)
    }
}

interface BalanceSetUseCase {
    suspend fun invoke(balance: BalanceEntity)
}