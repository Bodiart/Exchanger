package com.bodiart.exchanger.feature.exchanger.presentation

data class ExchangerViewState(
    val balances: List<Balance> = listOf(),
    val sellAmount: String = "",
    val sellSelectedCurrency: String = "",
    val receiveAmount: String = "",
    val receiveSelectedCurrency: String = "",
    val availableCurrencies: List<String> = listOf(),
    val isSubmitBtnEnabled: Boolean = false,
) {
    data class Balance(
        val balance: String,
        val currency: String
    )
}