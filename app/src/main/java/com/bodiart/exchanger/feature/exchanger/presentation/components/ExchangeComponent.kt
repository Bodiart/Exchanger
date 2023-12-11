@file:OptIn(ExperimentalMaterialApi::class)

package com.bodiart.exchanger.feature.exchanger.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ExchangeComponent(
    sellAmount: String,
    onSellAmountChanged: (String) -> Unit,
    sellSelectedCurrency: String,
    onSellCurrencySelected: (String) -> Unit,
    receiveAmount: String,
    receiveSelectedCurrency: String,
    availableCurrencies: List<String>,
    onReceiveCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Currency exchange",
            modifier = Modifier.padding(16.dp) // use res
        )

        ExchangeSection(
            iconRes = android.R.drawable.arrow_up_float,
            iconBgColor = Color.Red,
            text = "Sell", // use res
            amount = sellAmount,
            selectedCurrency = sellSelectedCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = onSellCurrencySelected,
            modifier = Modifier.fillMaxWidth(),
            onAmountChanged = onSellAmountChanged
        )

        ExchangeSection(
            iconRes = android.R.drawable.arrow_down_float,
            iconBgColor = Color.Green,
            text = "Receive", // use res
            amount = receiveAmount,
            selectedCurrency = receiveSelectedCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = onReceiveCurrencySelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ExchangeSection(
    @DrawableRes iconRes: Int,
    iconBgColor: Color,
    text: String,
    amount: String,
    selectedCurrency: String,
    availableCurrencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAmountChanged: ((String) -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.padding(16.dp) // use res
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp) // use res
                .background(
                    color = iconBgColor,
                    shape = CircleShape
                )
                .padding(8.dp) // use res
                .align(Alignment.CenterVertically),
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 16.dp) // use res
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        // for now it handles only Integers
        BasicTextField(
            value = amount,
            onValueChange = { onAmountChanged?.invoke(it) },
            readOnly = onAmountChanged == null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(IntrinsicSize.Min),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
        )

        Dropdown(
            selectedCurrency = selectedCurrency,
            currencies = availableCurrencies,
            modifier = Modifier
                .padding(start = 16.dp) // use res
                .height(50.dp) // use res
                .width(120.dp) // use res
                .align(Alignment.CenterVertically),
            onCurrencySelected = onCurrencySelected
        )
    }
}

@Composable
private fun Dropdown(
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
        },
        modifier = modifier
    ) {
        TextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
        )
        
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    onClick = {
                        onCurrencySelected(currency)
                        isExpanded = false
                    }
                ) {
                    Text(text = currency)
                }
            }
        }
    }
}