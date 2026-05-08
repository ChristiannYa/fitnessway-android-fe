package com.example.fitnessway.util.date_time

import com.example.fitnessway.data.state.timezone.ITimezoneStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class AppDateTimeFormatter(
    timezoneStateHolder: ITimezoneStateHolder,
    scope: CoroutineScope
) : IAppDateTimeFormatter {

    private data class Formatters(
        val display: DateTimeFormatter,
        val kebab: DateTimeFormatter,
        val time: DateTimeFormatter
    )

    private val formatters: StateFlow<Formatters?> = timezoneStateHolder.state
        .mapNotNull { it.timezone }
        .map { timezone ->
            Formatters(
                display = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(timezone),
                kebab = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(timezone),
                time = DateTimeFormatter.ofPattern("hh:mm a").withZone(timezone),
            )
        }
        .stateIn(scope, SharingStarted.Eagerly, null)

    private fun formatters() =
        formatters.value ?: throw IllegalStateException("[AppDateTimeFormatter] Timezone not set")

    private val timezone = timezoneStateHolder.state.value.timezone

    override fun formatDisplayDate(instant: Instant): String = formatters().display.format(instant.toJavaInstant())
    override fun formatKebabDate(instant: Instant): String = formatters().kebab.format(instant.toJavaInstant())
    override fun formatTime(instant: Instant): String = formatters().time.format(instant.toJavaInstant())

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