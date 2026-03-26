package com.example.fitnessway.util

fun String.toPascalCaseSpaced(): String = this
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
    .replaceFirstChar { it.uppercaseChar() }

fun String.plural(): String = this + "s"