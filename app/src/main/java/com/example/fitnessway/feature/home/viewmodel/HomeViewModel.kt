package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.home.manager.IHomeManagers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val nutrientRepo: INutrientRepository,
    private val foodRepo: IFoodRepository,
    private val managers: IHomeManagers,
    userStateHolder: IUserStateHolder
) : ViewModel() {
    // General
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val user = userStateHolder.userState.value.user

    // Date Managers
    val selectedDate: StateFlow<Date> = managers.date.selectedDate

    fun getFormattedDay(date: Date): String = managers.date.getFormattedDay(date)
    fun changeDay(days: Int) = managers.date.changeDay(days)
    fun getCurrentTime() = managers.date.getCurrentTime()

    // Food Managers
    val foodLogCategory: StateFlow<String> = managers.foodLog.foodLogCategory
    fun setFoodLogCategory(categories: FoodLogCategories) =
        managers.foodLog.setFoodLogCategory(categories)

    val selectedFood = managers.foodLog.selectedFood
    fun setSelectedFood(food: FoodInformation) = managers.foodLog.setSelectedFood(food)

    // Repository calls
    fun getNutrientIntakes() {
        val apiDate = managers.date.getApiFormattedDate()

        viewModelScope.launch {
            nutrientRepo.getNutrientIntakes(apiDate).collect { state ->
                _uiState.update { it.copy(nutrientIntakesState = state) }
            }
        }
    }

    fun getFoods() {
        viewModelScope.launch {
            foodRepo.getFoods().collect { state ->
                _uiState.update { it.copy(foodsState = state) }
            }
        }
    }

    fun getFoodLogs() {
        val apiDate = managers.date.getApiFormattedDate()

        viewModelScope.launch {
            foodRepo.getFoodLogs(apiDate).collect { state ->
                _uiState.update { it.copy(foodLogsState = state) }
            }
        }
    }
}