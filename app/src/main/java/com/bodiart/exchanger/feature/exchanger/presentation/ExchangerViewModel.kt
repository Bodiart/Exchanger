package com.bodiart.exchanger.feature.exchanger.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bodiart.exchanger.feature.exchanger.domain.model.BalanceEntity
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CanExchangeProceedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrenciesGetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.CurrencySelectedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangeProceedUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataCurrenciesUpdateUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ExchangerDataInitializeUseCase
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrenciesEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.CurrencyOperationEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangeProceedResultEntity
import com.bodiart.exchanger.feature.exchanger.domain.model.ExchangerDataEntity
import com.bodiart.exchanger.feature.exchanger.domain.usecase.BalanceGetUseCase
import com.bodiart.exchanger.feature.exchanger.domain.usecase.ReceiveAmountCalculateUseCase
import com.bodiart.exchanger.feature.exchanger.util.DEFAULT_AMOUNT
import com.bodiart.exchanger.feature.exchanger.util.extension.formatAmount
import com.bodiart.exchanger.feature.exchanger.util.extension.formatBalance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ExchangerViewModel(
    currenciesGetUseCase: CurrenciesGetUseCase,
    balanceGetUseCase: BalanceGetUseCase,
    private val exchangerDataInitializeUseCase: ExchangerDataInitializeUseCase,
    private val exchangerDataCurrenciesUpdateUseCase: ExchangerDataCurrenciesUpdateUseCase,
    private val currencySelectedUseCase: CurrencySelectedUseCase,
    private val exchangeProceedUseCase: ExchangeProceedUseCase,
    private val canExchangeProceedUseCase: CanExchangeProceedUseCase,
    private val receiveAmountCalculateUseCase: ReceiveAmountCalculateUseCase
) : ViewModel() {
    // MVI pattern (can be used MVVM, ui can subscribe exchangerData and balance)
    private val viewStateMutableFlow = MutableStateFlow(ExchangerViewState())
    private val eventsMutableFlow = MutableSharedFlow<ExchangerViewEvent>()

    private val exchangerData = MutableStateFlow<ExchangerDataEntity?>(null)

    private val currencies: StateFlow<CurrenciesEntity?> = currenciesGetUseCase.invoke()
        .onEach { currencies ->
            if (exchangerData.value == null) {
                exchangerData.tryEmit(exchangerDataInitializeUseCase.invoke(currencies))
            } else {
                exchangerData.tryEmit(
                    exchangerDataCurrenciesUpdateUseCase.invoke(
                        requireNotNull(exchangerData.value),
                        currencies
                    )
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val balance = balanceGetUseCase.invoke()
        .onEach { refreshBalanceViewState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BalanceEntity(listOf()))

    val viewStateFlow = viewStateMutableFlow.asStateFlow()
    val eventsFlow = eventsMutableFlow.asSharedFlow()

    init {
        collectExchangeData()
    }

    fun onSellCurrencySelected(currency: String) {
        exchangerData.value?.let {
            exchangerData.tryEmit(
                currencySelectedUseCase.invoke(it, currency, CurrencyOperationEntity.SELL)
                    .calculateReceiveAmount()
            )
        }
    }

    fun onReceiveCurrencySelected(currency: String) {
        exchangerData.value?.let {
            exchangerData.tryEmit(
                currencySelectedUseCase.invoke(it, currency, CurrencyOperationEntity.RECEIVE)
                    .calculateReceiveAmount()
            )
        }
    }

    fun onSubmitClicked() = viewModelScope.launch {
        exchangerData.value?.let {
            when (val result = exchangeProceedUseCase.invoke(it, balance.value)) {
                is ExchangeProceedResultEntity.Success -> {
                    eventsMutableFlow.emit(
                        ExchangerViewEvent.ShowSuccessMessage(
                            sellCurrency = result.sellCurrency,
                            sellAmount = result.sellAmount.formatAmount(),
                            receiveCurrency = result.receiveCurrency,
                            receiveAmount = result.receiveAmount.formatAmount(),
                            fee = result.fee?.formatAmount()
                        )
                    )
                }
                is ExchangeProceedResultEntity.Failure -> {
                    eventsMutableFlow.emit(
                        ExchangerViewEvent.ShowFailureMessage(result.reason)
                    )
                }
            }
        }
    }

    fun onSellAmountChanged(amount: String) {
        val newSellAmount = amount.toBigDecimalOrNull() ?: BigDecimal(DEFAULT_AMOUNT)
        // we can limit length of amount input
        exchangerData.value?.let {
            exchangerData.tryEmit(
                it.calculateReceiveAmount(newSellAmount = newSellAmount)
            )
        }
    }

    private fun collectExchangeData() = viewModelScope.launch {
        exchangerData.collectLatest { data ->
            data?.let { refreshExchangerViewState(it) }
        }
    }

    private fun refreshExchangerViewState(exchangerData: ExchangerDataEntity) {
        viewStateMutableFlow.update { state ->
            state.copy(
                sellAmount = exchangerData.sellAmount.formatAmount(),
                sellSelectedCurrency = exchangerData.sellCurrency,
                receiveAmount = exchangerData.receiveAmount.formatAmount(),
                receiveSelectedCurrency = exchangerData.receiveCurrency,
                availableCurrencies = exchangerData.availableCurrencies,
                isSubmitBtnEnabled = canExchangeProceedUseCase.invoke(exchangerData, balance.value)
            )
        }
    }

    private fun refreshBalanceViewState(balance: BalanceEntity) {
        viewStateMutableFlow.update { state ->
            state.copy(
                balances = balance.currenciesBalance.map {
                    ExchangerViewState.Balance(
                        balance = it.balance.formatBalance(),
                        currency = it.currency
                    )
                }.sortedBy { it.currency }
            )
        }
    }

    private fun ExchangerDataEntity.calculateReceiveAmount(
        newSellAmount: BigDecimal? = null
    ): ExchangerDataEntity {
        return copy(
            sellAmount = newSellAmount ?: sellAmount,
            receiveAmount = currencies.value?.let { currencies ->
                receiveAmountCalculateUseCase.invoke(
                    sellCurrency = sellCurrency,
                    receiveCurrency = receiveCurrency,
                    sellAmount = newSellAmount ?: sellAmount,
                    currencies = currencies
                )
            } ?: BigDecimal(DEFAULT_AMOUNT)
        )
    }
}