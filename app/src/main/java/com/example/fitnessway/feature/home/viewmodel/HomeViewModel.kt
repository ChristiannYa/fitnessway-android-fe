package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodAddInfoApiFormat
import com.example.fitnessway.data.model.food.FoodAddNutrientAmountApiFormat
import com.example.fitnessway.data.model.food.FoodAddRequest
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogUpdateRequest
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.IApplicationStateStore
import com.example.fitnessway.feature.home.manager.IHomeManagers
import com.example.fitnessway.feature.home.manager.date.IDateManager
import com.example.fitnessway.feature.home.manager.food.IFoodManager
import com.example.fitnessway.feature.home.manager.foodlog.IFoodLogManager
import com.example.fitnessway.feature.home.manager.ui.IUiManager
import com.example.fitnessway.util.Food.calcNutrientsBasedOnFoodLogServings
import com.example.fitnessway.util.Food.subtractNutrientsFromIntakes
import com.example.fitnessway.util.Ui
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
    val appStateStore: IApplicationStateStore
) : ViewModel(),
    IFoodManager by managers.food,
    IFoodLogManager by managers.foodLog,
    IDateManager by managers.date,
    IUiManager by managers.ui {
    val user = appStateStore.userStateHolder.userState.value.user

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val nutrientRepoUiState = nutrientRepo.uiState
    val foodRepoUiState = foodRepo.uiState

    fun getNutrientIntakes() {
        val date = managers.date.getApiFormattedDate()
        nutrientRepo.loadNutrientIntakes(date)
    }

    fun getFoodLogs() {
        val date = managers.date.getApiFormattedDate()
        foodRepo.loadFoodLogs(date)
    }

    fun getFoods() {
        foodRepo.loadFoods()
    }

    fun getNutrients() {
        nutrientRepo.loadNutrients()
    }

    fun addFood() {
        val user = user ?: return
        val formState = managers.food.foodCreationFormState.value

        // @TODO: Make the request optimistic

        val request = FoodAddRequest(
            userId = user.id,
            information = FoodAddInfoApiFormat(
                name = formState.name,
                brand = formState.brand,
                amountPerServing = formState.amountPerServing.toDoubleOrNull() ?: 0.0,
                servingUnit = formState.servingUnit
            ),
            nutrients = formState.nutrients
                .filter { (_, amount) -> (amount.toDoubleOrNull() ?: 0.0) > 0 }
                .map { (nutrientId, amount) ->
                    FoodAddNutrientAmountApiFormat(
                        nutrientId = nutrientId,
                        amount = amount.toDoubleOrNull() ?: 0.0
                    )
                }
        )

        viewModelScope.launch {
            foodRepo.addFood(request).collect { state ->
                _uiState.update { it.copy(foodAddState = state) }
            }
        }
    }

    fun resetFoodAddState() {
        _uiState.update { it.copy(foodAddState = UiState.Idle) }
    }

    fun addFoodLog() {
        // Check for data states before allowing request
        val user = user ?: return
        val foodLogFormState = managers.foodLog.foodLogFormState.value ?: return
        val selectedFood = managers.foodLog.selectedFoodToLog.value ?: return

        val date = managers.date.getApiFormattedDate()

        val request = FoodLogAddRequest(
            userId = user.id,
            foodId = selectedFood.information.id,
            servings = foodLogFormState.data.servings.toDouble(),
            category = managers.foodLog.foodLogCategory.value,
            time = "$date ${foodLogFormState.data.time}"
        )

        viewModelScope.launch {
            foodRepo.addFoodLog(request, date).collect { state ->

                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogAddState = state) }

                        foodRepo.refreshFoodLogs(date)
                        nutrientRepo.refreshNutrientIntakes(date)
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodLogAddState = state) }
                    }

                    is UiState.Loading -> {
                        _uiState.update { it.copy(foodLogAddState = state) }
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateFoodLog() {
        // Check for states before proceeding
        val user = user ?: return
        val formState = managers.foodLog.foodLogEditionFormState.value ?: return
        val selectedFoodLog = managers.foodLog.selectedFoodLog.value ?: return

        val date = managers.date.getApiFormattedDate()

        // Get current food logs data to update optimistically
        val currentFoodLogsState = foodRepo.uiState.value.foodLogsCache[date]

        // Only proceed if there are food logs
        if (currentFoodLogsState !is UiState.Success) return

        // Gather new nutrient data based on amount per servings
        val newNutrientData = calcNutrientsBasedOnFoodLogServings(
            nutrients = selectedFoodLog.food.nutrients,
            currentServings = selectedFoodLog.servings,
            newServings = formState.data.servings.toDouble()
        )

        // Update the current food log's nutrients
        val foodWithUpdatedNutrients = selectedFoodLog.food.copy(
            nutrients = newNutrientData
        )

        // Create the updated food log information
        val updatedFoodLog = FoodLogData(
            id = selectedFoodLog.id,
            category = selectedFoodLog.category,
            time = selectedFoodLog.time,
            servings = formState.data.servings.toDouble(),
            foodStatus = selectedFoodLog.foodStatus,
            foodSnapshotId = selectedFoodLog.foodSnapshotId,
            food = foodWithUpdatedNutrients
        )

        // Change the selected food log's nutrients value
        managers.foodLog.setSelectedFoodLog(updatedFoodLog)

        val request = FoodLogUpdateRequest(
            userId = user.id,
            foodLogId = updatedFoodLog.id,
            foodSnapshotId = updatedFoodLog.foodSnapshotId,
            servings = updatedFoodLog.servings
        )

        // Send the api request
        viewModelScope.launch {
            foodRepo.updateFoodLog(request, date).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogUpdateState = state) }

                        nutrientRepo.refreshNutrientIntakes(date)
                        foodRepo.refreshFoodLogs(date)
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodLogUpdateState = state) }
                        managers.foodLog.setSelectedFoodLog(selectedFoodLog)
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteFoodLog() {
        val selectedFoodLogToRemove = managers.foodLog.selectedFoodLogToRemove.value ?: return
        val formattedDate = managers.date.getApiFormattedDate()

        // Get current data to update optimistically
        val currentFoodLogsState = foodRepo.uiState.value.foodLogsCache[formattedDate]
        val currentNutrientIntakesState =
            nutrientRepo.uiState.value.nutrientIntakesCache[formattedDate]

        // Only proceed if we have data
        if (currentFoodLogsState !is UiState.Success ||
            currentNutrientIntakesState !is UiState.Success
        ) return

        val currentFoodLogs = currentFoodLogsState.data
        val currentNutrientIntakes = currentNutrientIntakesState.data

        // Optimistically update food logs UI by filtering out the food log
        val optimisticFoodLogs = FoodLogsByCategory(
            breakfast = currentFoodLogs.breakfast.filter { it.id != selectedFoodLogToRemove.id },
            lunch = currentFoodLogs.lunch.filter { it.id != selectedFoodLogToRemove.id },
            dinner = currentFoodLogs.dinner.filter { it.id != selectedFoodLogToRemove.id },
            supplement = currentFoodLogs.supplement.filter { it.id != selectedFoodLogToRemove.id }
        )

        val optimisticNutrientIntakes = subtractNutrientsFromIntakes(
            currentIntakes = currentNutrientIntakes,
            foodLog = selectedFoodLogToRemove
        )

        // Update UI immediately
        foodRepo.updateState {
            it.copy(
                foodLogsCache = it.foodLogsCache + (formattedDate to UiState.Success(
                    optimisticFoodLogs
                ))
            )
        }

        nutrientRepo.updateState {
            it.copy(
                nutrientIntakesCache = it.nutrientIntakesCache + (formattedDate to UiState.Success(
                    optimisticNutrientIntakes
                ))
            )
        }

        viewModelScope.launch {
            foodRepo.deleteFoodLog(
                foodLogId = selectedFoodLogToRemove.id,
                date = formattedDate
            ).collect { state ->

                when (state) {
                    is UiState.Success -> _uiState.update { it.copy(foodLogDeleteState = state) }

                    is UiState.Error -> {
                        // Revert back to original state on error
                        _uiState.update { it.copy(foodLogDeleteState = state) }

                        foodRepo.updateState {
                            it.copy(
                                foodLogsCache = it.foodLogsCache + (formattedDate to UiState.Success(
                                    currentFoodLogs
                                ))
                            )
                        }

                        nutrientRepo.updateState {
                            it.copy(
                                nutrientIntakesCache = it.nutrientIntakesCache + (formattedDate to UiState.Success(
                                    currentNutrientIntakes
                                ))
                            )
                        }
                    }

                    else -> {}
                }

            }
        }
    }

    fun resetFoodLogAddState() {
        _uiState.update { it.copy(foodLogAddState = UiState.Idle) }
    }

    fun resetFoodLogUpdateState() {
        _uiState.update { it.copy(foodLogUpdateState = UiState.Idle) }
    }

    fun resetFoodLogDeleteState() {
        _uiState.update { it.copy(foodLogDeleteState = UiState.Idle) }
    }
}