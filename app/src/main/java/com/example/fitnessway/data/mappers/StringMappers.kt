package com.example.fitnessway.data.mappers

fun String.toPascalCaseSpaced(
    shouldLowerCases: Boolean = true
): String = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .let { if (shouldLowerCases) it.lowercase() else it }
    .replaceFirstChar { it.uppercaseChar() }

fun String.plural(): String = this + "s"