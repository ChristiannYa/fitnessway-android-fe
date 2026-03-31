package com.example.fitnessway.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

private val UI_DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a", Locale.US)

fun String.toInstant(zone: ZoneId = ZoneId.systemDefault()): Instant =
    LocalDateTime
        .parse(this, UI_DATE_FORMAT)
        .atZone(zone)
        .toInstant()
        .toKotlinInstant()