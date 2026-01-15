package com.example.fitnessway.util

import android.util.Log
import com.example.fitnessway.util.Constants.LogLevel
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign

object Formatters {
    private val formatCache = mutableMapOf<Int, DecimalFormat>()

    fun doubleFormatter(value: Double, decimalPlaces: Int = 1): String {
        require(decimalPlaces >= 0) { "Decimal places must be non-negative" }

        // Handle near-zero values
        val normalizedValue = if (abs(value) < 1e-10) 0.0 else value

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

    fun Double.roundIfClose(threshold: Double = 0.01): Double {
        val nearestWhole = round(this)

        return if (abs(this - nearestWhole) < threshold) {
            nearestWhole
        } else this
    }

    fun validateDoubleAsString(
        doubleAsString: String,
        itemToBeValidated: String? = "Value"
    ): String? {
        return if (doubleAsString.isEmpty()) null else {
            val amount = doubleAsString.toDoubleOrNull()

            val error = if (amount == null) "$itemToBeValidated must be provided" else {
                if (amount > 0.0) null else "$itemToBeValidated must be greater than 0"
            }

            error
        }
    }

    private val SERVER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
    private val READABLE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    private val SERVER_FORMAT_REGEX = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{6}""")

    fun transformServerDate(dateString: String): String {
        val dateTime = LocalDateTime.parse(dateString, SERVER_DATE_FORMAT)
        return dateTime.format(READABLE_DATE_FORMAT)
    }

    fun getCurrentDateInServerFormat(): String {
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(SERVER_DATE_FORMAT)
    }

    fun serverDateToReadableAppDate(dateString: String): String {
        return if (SERVER_FORMAT_REGEX.matches(dateString)) {
            transformServerDate(dateString)
        } else dateString
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

    fun <T> formatUiErrorMessage(uiState: UiState<T>): String {
        val backupErrorMessage = "An unknown error occurred"
        return (uiState as? UiState.Error)?.message ?: backupErrorMessage
    }

    fun String.snakeToReadableText(): String {
        return this.split("_").joinToString(" ") { outer ->
            outer.replaceFirstChar { inner ->
                if (inner.isLowerCase()) inner.titlecase(
                    getDefault()
                ) else inner.toString()
            }
        }
    }

    fun logcat(
        message: String,
        level: LogLevel = LogLevel.DEBUG
    ) {
        when (level) {
            LogLevel.DEBUG -> Log.d(Constants.DEBUG_TAG, message)
            LogLevel.ERROR -> Log.e(Constants.DEBUG_TAG, message)
            LogLevel.INFO -> Log.i(Constants.DEBUG_TAG, message)
            LogLevel.WARN -> Log.w(Constants.DEBUG_TAG, message)
            LogLevel.VERBOSE -> Log.v(Constants.DEBUG_TAG, message)
        }
    }
}