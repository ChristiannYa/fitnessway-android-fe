package com.example.fitnessway.util

import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.pow

object Formatters {
    private val formatCache = mutableMapOf<Int, DecimalFormat>()

    fun doubleFormatter(value: Double, decimalPlaces: Int = 1): String {
        require(decimalPlaces >= 0) { "Decimal places must be non-negative" }

        val multiplier = 10.0.pow(decimalPlaces)
        val truncated = floor(value * multiplier) / multiplier

        val decimalFormat = formatCache.getOrPut(decimalPlaces) {
            val pattern = if (decimalPlaces == 0) "#" else "#.${"#".repeat(decimalPlaces)}"
            DecimalFormat(pattern)
        }

        return decimalFormat.format(truncated)
    }
}