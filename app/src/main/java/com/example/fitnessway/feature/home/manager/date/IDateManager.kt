package com.example.fitnessway.feature.home.manager.date

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Instant

interface IDateManager {
    val selectedDate: StateFlow<Instant>
    fun changeDay(days: Int): Unit
}