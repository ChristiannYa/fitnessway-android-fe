package com.example.fitnessway.data.mappers

fun String.toPascalSpaced(
    shouldLowerCases: Boolean = true
) = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .let { if (shouldLowerCases) it.lowercase() else it }
    .replaceFirstChar { it.uppercaseChar() }

// @TODO: Rename to `toPlural`
fun String.plural(): String = this + "s"