package com.example.fitnessway.feature.home.manager.date

import com.example.fitnessway.data.state.timezone.ITimezoneStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class DateManager(timezoneStateHolder: ITimezoneStateHolder) : IDateManager {
    
    private val timezone = timezoneStateHolder.state.value.timezone

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