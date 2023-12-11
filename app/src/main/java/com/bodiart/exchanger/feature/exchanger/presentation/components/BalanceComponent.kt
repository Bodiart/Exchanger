package com.bodiart.exchanger.feature.exchanger.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bodiart.exchanger.feature.exchanger.presentation.ExchangerViewState

@Composable
fun BalanceComponent(
    balances: List<ExchangerViewState.Balance>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "My balances", // use res
            modifier = Modifier.padding(16.dp) // use res
        )

        LazyRow {
            items(
                items = balances,
                key = { it.currency }
            ) { item ->
                BalanceItem(item)
            }
        }
    }
}

@Composable
private fun BalanceItem(item: ExchangerViewState.Balance) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp) // use res
    ) {
        Text(text = item.balance)
        Spacer(modifier = Modifier.size(8.dp)) // use res
        Text(text = item.currency)
    }
}