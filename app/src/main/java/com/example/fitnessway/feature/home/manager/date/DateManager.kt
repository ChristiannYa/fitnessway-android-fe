package com.example.fitnessway.feature.home.manager.date

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class DateManager(private val timezone: ZoneId) : IDateManager {
    private val _selectedDate = MutableStateFlow<Instant>(Clock.System.now())
    override val selectedDate: StateFlow<Instant> = _selectedDate

    override fun changeDay(days: Int) {
        _selectedDate.value = _selectedDate.value
            .toJavaInstant()
            .atZone(timezone)
            .plusDays(days.toLong())
            .toInstant()
            .toKotlinInstant()
    }
}