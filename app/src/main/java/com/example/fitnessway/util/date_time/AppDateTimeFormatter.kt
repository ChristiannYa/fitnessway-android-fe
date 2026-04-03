package com.example.fitnessway.util.date_time

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class AppDateTimeFormatter(private val timezone: ZoneId) : IAppDateTimeFormatter {
    private val displayDateFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone(timezone)

    private val kebabDateFormatter = DateTimeFormatter
        .ofPattern("MM-dd-yyyy")
        .withZone(timezone)

    private val timeFormatter = DateTimeFormatter
        .ofPattern("hh:mm a")
        .withZone(timezone)

    override fun formatDisplayDate(instant: Instant): String = displayDateFormatter.format(instant.toJavaInstant())
    override fun formatKebabDate(instant: Instant): String = kebabDateFormatter.format(instant.toJavaInstant())
    override fun formatTime(instant: Instant): String = timeFormatter.format(instant.toJavaInstant())

    override fun getCurrentTime(): String = formatTime(Clock.System.now())
    override fun getDayDisplay(instant: Instant): String {
        val selectedDate = instant
            .toJavaInstant()
            .atZone(timezone)
            .toLocalDate()

        val today = LocalDate.now(timezone)
        val daysDiff = ChronoUnit.DAYS.between(today, selectedDate).toInt()

        return when (daysDiff) {
            -1 -> "Yesterday"
            0 -> "Today"
            1 -> "Tomorrow"
            else -> formatDisplayDate(instant)
        }
    }
}