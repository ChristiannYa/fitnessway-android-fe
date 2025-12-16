package com.example.fitnessway.util

import android.util.Log
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

    fun validateDoubleAsString(
        doubleAsString: String,
        itemToBeValidated: String? = "Value"
    ): String? {
        return if (doubleAsString.isEmpty()) null else {
            val amount = doubleAsString.toDoubleOrNull()

            if (amount == null) "$itemToBeValidated must be provided" else {
                if (amount > 0.0) null else "$itemToBeValidated must be greater than 0"
            }
        }
    }

    /**
     * Converts a user input string to a Double, with stricter validation than [toDoubleOrNull].
     *
     * This function is designed for validating numeric user input and rejects Kotlin/Java
     * Double literal suffixes (like 'd', 'f') and scientific notation that [toDoubleOrNull]
     * would accept.
     *
     * Accepted formats:
     * - Positive integers: "123"
     * - Negative integers: "-123"
     * - Positive decimals: "123.45" or ".5"
     * - Negative decimals: "-123.45"
     *
     * Rejected formats:
     * - Decimal-only with minus: "-.5"
     * - Double/Float literals: "123d", "123f"
     * - Scientific notation: "1.23e5"
     * - Invalid characters: "123abc"
     *
     * @return The parsed Double value, or null if the string doesn't match the expected format
     */
    fun String.toInputDouble(): Double? {
        return if (this.matches(Regex("^-?\\d+(\\.\\d+)?$|^\\.\\d+$"))) {
            this.toDoubleOrNull()
        } else {
            null
        }
    }

    fun logcat(message: String) {
        Log.d(Constants.DEBUG_TAG, message)
    }
}