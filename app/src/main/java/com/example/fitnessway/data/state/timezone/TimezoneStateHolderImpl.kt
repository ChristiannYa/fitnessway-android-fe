package com.example.fitnessway.data.state.timezone

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId

class TimezoneStateHolderImpl : ITimezoneStateHolder {

    private val _state = MutableStateFlow(TimezoneState())
    override val state: StateFlow<TimezoneState> = _state

    override fun setTimezone(timezone: ZoneId) {
        _state.value = TimezoneState(timezone = timezone)
    }
}