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
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UFood.calcNutrientIntakesFromFoodLog
import com.example.fitnessway.util.UFood.calcNutrientIntakesFromFoodLogServings
import com.example.fitnessway.util.UFood.mapFoodLogs
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

    fun getNutrientIntakes() {
        val date = managers.date.getApiFormattedDate()
        nutrientRepo.loadNutrientIntakes(date)
    }

    fun getFoods() {
        foodRepo.loadFoods()
    }

    fun getFoodLogs() {
        val date = managers.date.getApiFormattedDate()
        foodRepo.loadFoodLogs(date)
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

        val apiDate = managers.date.getApiFormattedDate()

        // Get original data to update optimistically
        val originalFoodLogsState = foodRepo.uiState.value.foodLogsCache[apiDate]
        val originalNutrientIntakesState = nutrientRepo.uiState.value.nutrientIntakesCache[apiDate]

        // Only proceed if there are food logs
        if (originalFoodLogsState !is UiState.Success ||
            originalNutrientIntakesState !is UiState.Success
        ) return

        val originalFoodLogs = originalFoodLogsState.data
        val originalNutrientIntakes = originalNutrientIntakesState.data

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


        // Create optimistic food logs
        val optimisticFoodLogs = originalFoodLogs.mapFoodLogs { category, logs ->
            logs.filter { it.id != selectedFoodLog.id }.let { filteredLogs ->
                if (category.name.lowercase() == updatedFoodLog.category) {
                    filteredLogs + updatedFoodLog
                } else filteredLogs
            }
        }

        // Create optimistic nutrient intakes
        val intakesWithoutFoodLog = calcNutrientIntakesFromFoodLog(
            currentIntakes = originalNutrientIntakes,
            foodLog = selectedFoodLog,
            operation = UFood.FoodNutrientIntakesOperation.SUBTRACT
        )

        val optimisticIntakes = calcNutrientIntakesFromFoodLog(
            currentIntakes = intakesWithoutFoodLog,
            foodLog = updatedFoodLog,
            operation = UFood.FoodNutrientIntakesOperation.ADD
        )

        val request = FoodLogUpdateRequest(
            userId = user.id,
            foodLogId = updatedFoodLog.id,
            foodSnapshotId = updatedFoodLog.foodSnapshotId,
            servings = updatedFoodLog.servings
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
                    optimisticIntakes
                ))
            )
        }

        // Send the api request
        viewModelScope.launch {
            foodRepo.updateFoodLog(request, apiDate).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogUpdateState = state) }
                    }

                    is UiState.Error -> {
                        // Provide feedback to UI
                        _uiState.update { it.copy(foodLogUpdateState = state) }

                        // Change updated food to the previously selected food log
                        managers.foodLog.setSelectedFoodLog(selectedFoodLog)

                        // Obtain updated UI states after optimistic update
                        val currentFoodLogsState = foodRepo.uiState.value
                            .foodLogsCache[apiDate]

                        val currentNutrientIntakesState = nutrientRepo.uiState.value
                            .nutrientIntakesCache[apiDate]

                        if (currentFoodLogsState is UiState.Success) {
                            val revertedFoodLogs = currentFoodLogsState.data
                                .mapFoodLogs { category, logs ->
                                    (logs.filter { it.id != updatedFoodLog.id }
                                        .let { filteredLogs ->
                                            if (category.name.lowercase() == selectedFoodLog.category) {
                                                filteredLogs + selectedFoodLog
                                            } else filteredLogs
                                        })
                                        .sortedByDescending { it.id }
                                }

                            foodRepo.updateState {
                                it.copy(
                                    foodLogsCache = it.foodLogsCache +
                                            (apiDate to UiState.Success(revertedFoodLogs))
                                )
                            }
                        }

                        if (currentNutrientIntakesState is UiState.Success) {
                            val intakesWithoutUpdatedFoodLog = calcNutrientIntakesFromFoodLog(
                                currentIntakes = currentNutrientIntakesState.data,
                                foodLog = updatedFoodLog,
                                operation = UFood.FoodNutrientIntakesOperation.SUBTRACT
                            )

                            val revertedIntakes = calcNutrientIntakesFromFoodLog(
                                currentIntakes = intakesWithoutUpdatedFoodLog,
                                foodLog = selectedFoodLog,
                                operation = UFood.FoodNutrientIntakesOperation.ADD
                            )

                            nutrientRepo.updateState {
                                it.copy(
                                    nutrientIntakesCache = it.nutrientIntakesCache +
                                            (apiDate to UiState.Success(revertedIntakes))
                                )
                            }
                        }
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
        val originalIntakesState = nutrientRepo.uiState.value.nutrientIntakesCache[apiDate]

        // Only proceed if we have data
        if (originalFoodLogsState !is UiState.Success ||
            originalIntakesState !is UiState.Success
        ) return

        val originalFoodLogs = originalFoodLogsState.data
        val originalIntakes = originalIntakesState.data

        // Get or create the failed deletions set for this date
        val failedDeletionsForDate = _foodLogFailedDeletions.getOrPut(apiDate) { mutableSetOf() }

        // Remove current food log id from failed deletions list
        failedDeletionsForDate.remove(selectedFoodLogToRemove)

        // Optimistically update food logs UI by filtering out the food log
        val optimisticFoodLogs = originalFoodLogs.mapFoodLogs { _, logs ->
            logs.filter { it.id != selectedFoodLogToRemove.id }
        }

        val optimisticNutrientIntakes = calcNutrientIntakesFromFoodLog(
            currentIntakes = originalIntakes,
            foodLog = selectedFoodLogToRemove,
            operation = UFood.FoodNutrientIntakesOperation.SUBTRACT
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

                        val currentIntakesState = nutrientRepo.uiState.value
                            .nutrientIntakesCache[apiDate]

                        // Update food logs data after failed deletion
                        if (currentFoodLogsState is UiState.Success) {
                            val currentFoodLogs = currentFoodLogsState.data

                            // Revert back to original food logs state
                            val revertedFoodLogs = currentFoodLogs
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

                        if (currentIntakesState is UiState.Success) {
                            val currentIntakes = currentIntakesState.data

                            // Revert back to original nutrient intakes state
                            val revertedNutrients = calcNutrientIntakesFromFoodLog(
                                currentIntakes = currentIntakes,
                                foodLog = selectedFoodLogToRemove,
                                operation = UFood.FoodNutrientIntakesOperation.ADD
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