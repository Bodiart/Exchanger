package com.bodiart.exchanger.feature.exchanger.presentation

sealed class ExchangerViewEvent {
    data class ShowSuccessMessage(
        val sellCurrency: String,
        val sellAmount: String,
        val receiveCurrency: String,
        val receiveAmount: String,
        val fee: String?
    ) : ExchangerViewEvent()

    data class ShowFailureMessage(val message: String) : ExchangerViewEvent()
}