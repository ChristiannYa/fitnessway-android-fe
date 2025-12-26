package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogUpdateRequest
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.IApplicationStateStore
import com.example.fitnessway.feature.home.manager.IHomeManagers
import com.example.fitnessway.feature.home.manager.date.IDateManager
import com.example.fitnessway.feature.home.manager.foodlog.IFoodLogManager
import com.example.fitnessway.feature.home.manager.ui.IUiManager
import com.example.fitnessway.util.Food
import com.example.fitnessway.util.Food.calcNutrientIntakesFromFoodLog
import com.example.fitnessway.util.Food.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.Food.mapFoodLogs
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.logcat
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
    IFoodLogManager by managers.foodLog,
    IDateManager by managers.date,
    IUiManager by managers.ui {

    val user = appStateStore.userStateHolder.userState.value.user

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _foodLogFailedDeletions = mutableMapOf<String, MutableSet<FoodLogData>>()

    val nutrientRepoUiState = nutrientRepo.uiState
    val foodRepoUiState = foodRepo.uiState

    fun loadHomeData() {
        getNutrientIntakes()
        getFoodLogs()
    }

    fun refreshHomeData() {
        val date = managers.date.getApiFormattedDate()
        nutrientRepo.refreshNutrientIntakes(date)
        foodRepo.refreshFoodLogs(date)
    }

    fun getFoodLogs() {
        val date = managers.date.getApiFormattedDate()
        foodRepo.loadFoodLogs(date)
    }

    fun getFoods() {
        foodRepo.loadFoods()
    }

    fun getNutrientIntakes() {
        val date = managers.date.getApiFormattedDate()
        nutrientRepo.loadNutrientIntakes(date)
    }

    fun addFoodLog() {
        // Check for data states before allowing request
        val user = user ?: return
        val foodLogFormState = managers.foodLog.foodLogFormState.value ?: return
        val selectedFood = managers.foodLog.selectedFoodToLog.value ?: return
        val category = managers.foodLog.foodLogCategory.value ?: return

        val date = managers.date.getApiFormattedDate()

        val request = FoodLogAddRequest(
            userId = user.id,
            foodId = selectedFood.information.id,
            servings = foodLogFormState.data.servings.toDouble(),
            category = category.name.lowercase(),
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
        val newNutrientData = calcNutrientIntakesFromFoodLogServings(
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
            servings = doubleFormatter(
                value = formState.data.servingsPrecised,
                decimalPlaces = 4
            ).toDouble(),
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
        val apiDate = managers.date.getApiFormattedDate()

        // Get current data to update optimistically
        val originalFoodLogsState = foodRepo.uiState.value.foodLogsCache[apiDate]
        val originalNutrientIntakesState = nutrientRepo.uiState.value.nutrientIntakesCache[apiDate]

        // Only proceed if we have data
        if (originalFoodLogsState !is UiState.Success ||
            originalNutrientIntakesState !is UiState.Success
        ) return

        // Get or create the failed deletions set for this date
        val failedDeletionsForDate = _foodLogFailedDeletions.getOrPut(apiDate) { mutableSetOf() }

        // Remove current food log id from failed deletions list
        failedDeletionsForDate.remove(selectedFoodLogToRemove)

        val originalFoodLogs = originalFoodLogsState.data
        val originalNutrientIntakes = originalNutrientIntakesState.data

        // Optimistically update food logs UI by filtering out the food log
        val optimisticFoodLogs = originalFoodLogs.mapFoodLogs { _, logs ->
            logs.filter { it.id != selectedFoodLogToRemove.id }
        }

        val optimisticNutrientIntakes = calcNutrientIntakesFromFoodLog(
            currentIntakes = originalNutrientIntakes,
            foodLog = selectedFoodLogToRemove,
            operation = Food.FoodNutrientIntakesOperation.SUBTRACT
        )

        // Update UI immediately
        foodRepo.updateState {
            it.copy(
                foodLogsCache = it.foodLogsCache + (apiDate to UiState.Success(
                    optimisticFoodLogs
                ))
            )
        }

        nutrientRepo.updateState {
            it.copy(
                nutrientIntakesCache = it.nutrientIntakesCache + (apiDate to UiState.Success(
                    optimisticNutrientIntakes
                ))
            )
        }

        viewModelScope.launch {
            foodRepo.deleteFoodLog(
                foodLogId = selectedFoodLogToRemove.id,
                date = apiDate
            ).collect { state ->

                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogDeleteState = state) }

                        // No need to remove the food log id form the failed deletion list because
                        // it's already removed in the beginning of the function
                    }

                    is UiState.Error -> {
                        // Add failed deletion to failed deletions list
                        failedDeletionsForDate.add(selectedFoodLogToRemove)

                        // Provide feedback to UI
                        _uiState.update { it.copy(foodLogDeleteState = state) }

                        // Obtain updated UI states after optimistic update
                        val currentFoodLogsState = foodRepo.uiState.value
                            .foodLogsCache[apiDate]

                        val currentNutrientIntakesState = nutrientRepo.uiState.value
                            .nutrientIntakesCache[apiDate]

                        // Update food logs data after failed deletion
                        if (currentFoodLogsState is UiState.Success) {

                            // Revert back to original food logs state
                            val revertedFoodLogs = currentFoodLogsState.data
                                .mapFoodLogs { category, logs ->
                                    (logs + failedDeletionsForDate.filter {
                                        it.category.equals(
                                            other = category.name,
                                            ignoreCase = true
                                        ) // Safe string comparison
                                    })
                                        .distinctBy { it.id }
                                        .sortedByDescending { it.id }
                                }

                            foodRepo.updateState {
                                it.copy(
                                    foodLogsCache = it.foodLogsCache + (apiDate to UiState.Success(
                                        revertedFoodLogs
                                    ))
                                )
                            }
                        }

                        if (currentNutrientIntakesState is UiState.Success) {
                            // Revert back to original nutrient intakes state
                            val revertedNutrients = calcNutrientIntakesFromFoodLog(
                                currentIntakes = currentNutrientIntakesState.data,
                                foodLog = selectedFoodLogToRemove,
                                operation = Food.FoodNutrientIntakesOperation.ADD
                            )

                            nutrientRepo.updateState {
                                it.copy(
                                    nutrientIntakesCache = it.nutrientIntakesCache + (apiDate to UiState.Success(
                                        revertedNutrients
                                    ))
                                )
                            }
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