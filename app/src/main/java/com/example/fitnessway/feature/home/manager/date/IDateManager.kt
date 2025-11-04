package com.example.fitnessway.feature.home.manager.date

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface IDateManager {
    val selectedDate: StateFlow<Date>
    fun getCurrentTime(): String
    fun getFormattedDay(date: Date): String
    fun changeDay(days: Int): Unit
    fun getApiFormattedDate(): String
}