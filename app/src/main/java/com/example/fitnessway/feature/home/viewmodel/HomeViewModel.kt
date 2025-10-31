package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val nutrientRepo: INutrientRepository,
    private val foodRepo: IFoodRepository,
    userStateHolder: IUserStateHolder
) : ViewModel() {
    // General
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val user = userStateHolder.userState.value.user

    // Day picker related
    private val dateFormatter: DateFormat = SimpleDateFormat.getDateInstance()
    private val apiDateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.US)

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    fun getFormattedDay(date: Date): String {
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

    fun changeDay(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = _selectedDate.value
        calendar.add(Calendar.DAY_OF_YEAR, days)
        _selectedDate.value = calendar.time
    }

    private fun Calendar.clearTime() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // Food log related
    private val _foodLogCategory = MutableStateFlow("")
    val foodLogCategory: StateFlow<String> = _foodLogCategory

    fun setFoodLogCategory(categories: FoodLogCategories) {
        when (categories) {
            FoodLogCategories.BREAKFAST -> _foodLogCategory.value = "breakfast"
            FoodLogCategories.LUNCH -> _foodLogCategory.value = "lunch"
            FoodLogCategories.DINNER -> _foodLogCategory.value = "dinner"
            FoodLogCategories.SUPPLEMENT -> _foodLogCategory.value = "supplement"
        }
    }

    // Repository calls
    fun getNutrientIntakes() {
        val apiDate: String = apiDateFormatter.format(_selectedDate.value)

        viewModelScope.launch {
            nutrientRepo.getNutrientIntakes(apiDate).collect { state ->
                _uiState.update {
                    it.copy(nutrientIntakesState = state)
                }
            }
        }
    }

    fun getFoodLogs() {
        val apiDate: String = apiDateFormatter.format(_selectedDate.value)

        viewModelScope.launch {
            foodRepo.getFoodLogs(apiDate).collect { state ->
                _uiState.update {
                    it.copy(foodLogsState = state)
                }
            }
        }
    }
}