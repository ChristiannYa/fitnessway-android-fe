package com.example.fitnessway.util

inline fun <reified T : Enum<T>> String.isValidEnum(): Boolean =
    enumValues<T>().any { it.name.equals(this, ignoreCase = true) }

inline fun <reified T : Enum<T>> String.toEnum(): T =
    enumValues<T>().first { it.name.equals(this, ignoreCase = true) }

inline fun <reified T : Enum<T>> listEnumValues() =
    enumValues<T>().toList().joinToString(", ")