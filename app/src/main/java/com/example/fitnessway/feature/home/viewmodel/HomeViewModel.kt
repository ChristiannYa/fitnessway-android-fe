package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.home.manager.IHomeManagers
import com.example.fitnessway.feature.home.manager.date.IDateManager
import com.example.fitnessway.feature.home.manager.foodlog.IFoodLogManager
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val nutrientRepo: INutrientRepository,
    private val foodRepo: IFoodRepository,
    private val managers: IHomeManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    IFoodLogManager by managers.foodLog,
    IDateManager by managers.date {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val user = userStateHolder.userState.value.user

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

    fun addFoodLog() {
        // Check for data states before allowing request
        val user = user ?: return
        val foodLogFormState = managers.foodLog.foodLogFormState.value ?: return
        val selectedFood = managers.foodLog.selectedFood.value ?: return

        val foodLogFoodId = selectedFood.information.id
        val foodLogServings = foodLogFormState.data.servings.toDouble()
        val foodLogCategory = managers.foodLog.foodLogCategory.value
        val foodLogTime = "${managers.date.getApiFormattedDate()} ${foodLogFormState.data.time}"

        val request = FoodLogAddRequest(
            userId = user.id,
            foodId = foodLogFoodId,
            servings = foodLogServings,
            category = foodLogCategory,
            time = foodLogTime
        )

        viewModelScope.launch {
            foodRepo.addFoodLog(request).collect { state ->
                _uiState.update { it.copy(foodLogAddState = state) }
            }
        }
    }

    // State can only be updated through `_uiState.update()`
    fun resetFoodLogAddState() {
        _uiState.update { it.copy(foodLogAddState = UiState.Idle) }
    }
}