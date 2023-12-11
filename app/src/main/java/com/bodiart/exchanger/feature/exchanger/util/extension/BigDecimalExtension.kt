package com.bodiart.exchanger.feature.exchanger.util.extension

import java.math.BigDecimal
import java.text.DecimalFormat

private const val AMOUNT_MAX_DECIMAL = 2
private const val BALANCE_PATTERN = "0.00"

fun BigDecimal.formatAmount(minDecimal: Int = 0, maxDecimal: Int = AMOUNT_MAX_DECIMAL): String {
    return DecimalFormat().apply {
        minimumFractionDigits = minDecimal
        maximumFractionDigits = maxDecimal
        isGroupingUsed = false
    }.format(this)
}

fun BigDecimal.formatBalance(): String {
    return DecimalFormat(BALANCE_PATTERN).format(this)
}