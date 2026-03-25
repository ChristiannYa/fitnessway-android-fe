package com.example.fitnessway.util.extensions

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sign

private val formatCache = mutableMapOf<Int, DecimalFormat>()

fun Double.toTextAndPrecise(decimalPlaces: Int = 1): String {
    require(decimalPlaces >= 0) { "Decimal places must be non-negative" }

    // Handle near-zero values
    val normalizedValue = if (abs(this) < 1e-10) 0.0 else this

    val multiplier = 10.0.pow(decimalPlaces)
    val absValue = abs(normalizedValue)

    // Check if value is very close to a cleanly rounded number (floating point errors)
    val roundedToTarget = floor(absValue * multiplier + 0.5) / multiplier
    val difference = abs(absValue - roundedToTarget)

    // If within floating point error margin, use that rounded value
    // Otherwise, truncate to target decimal places
    val result = if (difference < 1e-9) {
        roundedToTarget * sign(normalizedValue)
    } else {
        floor(absValue * multiplier) / multiplier * sign(normalizedValue)
    }

    // Handle negative zero
    val finalValue = if (abs(result) < 1e-10) 0.0 else result

    val decimalFormat = formatCache.getOrPut(decimalPlaces) {
        val pattern = if (decimalPlaces == 0) "0" else "0.${"#".repeat(decimalPlaces)}"
        DecimalFormat(pattern)
    }

    return decimalFormat.format(finalValue)
}