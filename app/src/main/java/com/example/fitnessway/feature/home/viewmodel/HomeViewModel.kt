package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.home.manager.IHomeManagers
import com.example.fitnessway.feature.home.manager.date.IDateManager
import com.example.fitnessway.feature.home.manager.food.IFoodManager
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
    IFoodManager by managers.food,
    IFoodLogManager by managers.foodLog,
    IDateManager by managers.date {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val user = userStateHolder.userState.value.user


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
        val selectedFood = managers.foodLog.selectedFoodToLog.value ?: return

        val formatedDate = managers.date.getApiFormattedDate()

        val request = FoodLogAddRequest(
            userId = user.id,
            foodId = selectedFood.information.id,
            servings = foodLogFormState.data.servings.toDouble(),
            category = managers.foodLog.foodLogCategory.value,
            time = "$formatedDate ${foodLogFormState.data.time}"
        )

        viewModelScope.launch {
            foodRepo.addFoodLog(
                request = request,
                date = formatedDate
            ).collect { state ->
                _uiState.update { it.copy(foodLogAddState = state) }
            }
        }
    }

    fun resetFoodLogAddState() {
        _uiState.update { it.copy(foodLogAddState = UiState.Idle) }
    }

    fun deleteFoodLog() {
        val selectedFoodLogToRemove = managers.foodLog.selectedFoodLogToRemove.value ?: return
        val formattedDate = managers.date.getApiFormattedDate()

        // Get current food logs
        val currentFoodLogState = _uiState.value.foodLogsState

        // Only proceed if we have data
        if (currentFoodLogState !is UiState.Success) {
            return
        }

        val currentFoodLogs = currentFoodLogState.data

        // Optimistically update UI by filtering out the item
        val optimisticFoodLogs = FoodLogsByCategory(
            breakfast = currentFoodLogs.breakfast.filter { it.id != selectedFoodLogToRemove.id },
            lunch = currentFoodLogs.lunch.filter { it.id != selectedFoodLogToRemove.id },
            dinner = currentFoodLogs.dinner.filter { it.id != selectedFoodLogToRemove.id },
            supplement = currentFoodLogs.supplement.filter { it.id != selectedFoodLogToRemove.id }
        )

        // Update UI immediately
        _uiState.update { it.copy(foodLogsState = UiState.Success(optimisticFoodLogs)) }

        viewModelScope.launch {
            foodRepo.deleteFoodLog(
                foodLogId = selectedFoodLogToRemove.id,
                date = formattedDate
            ).collect { state ->

                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogDeleteState = state) }
                    }

                    is UiState.Error -> {
                        // Revert back to original state on error
                        _uiState.update {
                            it.copy(
                                foodLogsState = UiState.Success(currentFoodLogs),
                                foodLogDeleteState = state
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun resetFoodLogDeleteState() {
        _uiState.update { it.copy(foodLogDeleteState = UiState.Idle) }
    }
}