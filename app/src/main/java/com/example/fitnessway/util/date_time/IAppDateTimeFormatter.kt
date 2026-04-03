package com.example.fitnessway.util.date_time

import kotlin.time.Instant

interface IAppDateTimeFormatter {
    fun formatDisplayDate(instant: Instant): String
    fun formatKebabDate(instant: Instant): String
    fun formatTime(instant: Instant): String

    fun getCurrentTime(): String
    fun getDayDisplay(instant: Instant): String
}