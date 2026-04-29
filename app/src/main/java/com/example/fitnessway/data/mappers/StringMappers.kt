package com.example.fitnessway.data.mappers

fun String.toTitleCase(
    shouldLowerCases: Boolean = true
) = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .replace('_', ' ')
    .let { if (shouldLowerCases) it.lowercase() else it }
    .split(' ')
    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }