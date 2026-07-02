package com.example.fitnessway.data.mappers

fun String.toTitleCase(capitalizeAll: Boolean = true) = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .replace('_', ' ')
    .lowercase()
    .split(' ')
    .mapIndexed { i, word ->
        if (capitalizeAll || i == 0) {
            word.replaceFirstChar { it.uppercaseChar() }
        } else word
    }
    .joinToString(" ")
