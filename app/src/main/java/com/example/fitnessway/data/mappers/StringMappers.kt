package com.example.fitnessway.data.mappers

fun String.toTitleCase() = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .replace('_', ' ')
    .lowercase()
    .split(' ')
    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }