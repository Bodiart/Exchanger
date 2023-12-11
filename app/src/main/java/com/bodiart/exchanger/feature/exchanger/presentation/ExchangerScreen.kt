package com.bodiart.exchanger.feature.exchanger.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bodiart.exchanger.feature.exchanger.Di
import com.bodiart.exchanger.feature.exchanger.presentation.components.BalanceComponent
import com.bodiart.exchanger.feature.exchanger.presentation.components.ExchangeComponent

@Composable
fun ExchangerScreen(
    modifier: Modifier = Modifier,
    viewModel: ExchangerViewModel = Di.viewModel()
) {
    val context = LocalContext.current
    val viewState by viewModel.viewStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventsFlow.collect {
            handleViewEvent(
                event = it,
                context = context
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        BalanceComponent(
            balances = viewState.balances,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp) // use res
                .height(1.dp) // use res
                .background(Color.Black)
        )

        ExchangeComponent(
            sellAmount = viewState.sellAmount,
            onSellAmountChanged = viewModel::onSellAmountChanged,
            sellSelectedCurrency = viewState.sellSelectedCurrency,
            onSellCurrencySelected = viewModel::onSellCurrencySelected,
            receiveAmount = viewState.receiveAmount,
            receiveSelectedCurrency = viewState.receiveSelectedCurrency,
            availableCurrencies = viewState.availableCurrencies,
            onReceiveCurrencySelected = viewModel::onReceiveCurrencySelected,
            modifier = Modifier.fillMaxWidth()
        )

        // use imePadding()
        Button(
            onClick = viewModel::onSubmitClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // use res
            enabled = viewState.isSubmitBtnEnabled
        ) {
            Text(text = "Submit") // use res
        }
    }
}

private fun handleViewEvent(
    event: ExchangerViewEvent,
    context: Context
) {
    when (event) {
        is ExchangerViewEvent.ShowSuccessMessage -> {
            // use strings res
            var message = "You have converted ${event.sellAmount} ${event.sellCurrency}" +
                    " to ${event.receiveAmount} ${event.receiveCurrency}"
            event.fee?.let { fee ->
                message += ". Commission Fee - $fee ${event.sellCurrency}"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
        is ExchangerViewEvent.ShowFailureMessage -> {
            Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
        }
    }
}