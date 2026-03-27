package com.example.fitnessway.util

inline fun <reified T : Enum<T>> enumContains(name: String, ignoreCase: Boolean = true): Boolean =
    enumValues<T>().any { it.name.equals(name, ignoreCase = ignoreCase) }

inline fun <reified T : Enum<T>> String.toEnum(): T =
    enumValues<T>().first { it.name.equals(this, ignoreCase = true) }

inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? =
    this
        .takeIf { enumContains<T>(it) }
        ?.toEnum<T>()

inline fun <reified T : Enum<T>> listEnumValues() =
    enumValues<T>().map { it.name.lowercase() }