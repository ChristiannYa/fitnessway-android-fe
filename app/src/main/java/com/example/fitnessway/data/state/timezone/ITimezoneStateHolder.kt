package com.example.fitnessway.data.state.timezone

import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId

interface ITimezoneStateHolder {
    val state: StateFlow<TimezoneState>

    fun setTimezone(timezone: ZoneId)
}