package com.example.fitnessway.feature.home.manager.date

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateManager : IDateManager {
    private val dateFormatter: DateFormat = SimpleDateFormat.getDateInstance()
    private val apiDateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.US)
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

    private val _selectedDate = MutableStateFlow(Date())
    override val selectedDate: StateFlow<Date> = _selectedDate

    override fun getCurrentTime(): String {
        return timeFormatter.format(Date())
    }

    override fun getFormattedDay(date: Date): String {
        val selectedCal = Calendar.getInstance().apply {
            time = date
            clearTime()
        }
        val todayCal = Calendar.getInstance().apply { clearTime() }

        val daysDiff =
            ((selectedCal.timeInMillis - todayCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        return when (daysDiff) {
            0 -> "Today"
            -1 -> "Yesterday"
            1 -> "Tomorrow"
            else -> dateFormatter.format(date)
        }
    }

    override fun changeDay(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = _selectedDate.value
        calendar.add(Calendar.DAY_OF_YEAR, days)
        _selectedDate.value = calendar.time
    }

    override fun getApiFormattedDate(): String {
        return apiDateFormatter.format(_selectedDate.value)
    }

    private fun Calendar.clearTime() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}