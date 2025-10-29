package com.example.fitnessway.util

object Formatters {
    fun doubleFormatter(value: Double): String {
        return when {
            value == 0.0 -> "0"
            value % 1.0 == 0.0 -> value.toInt().toString() // whole number
            else -> "%.1f".format(value).trimEnd('0')
                .trimEnd('.') // 1 decimal, remove trailing zeros
        }
    }
}