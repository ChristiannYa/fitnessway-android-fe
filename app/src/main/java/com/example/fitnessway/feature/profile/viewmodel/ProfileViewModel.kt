package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.nutrient.NutrientColorsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithColor
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientPreferences
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.colors.IColorsManager
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import com.example.fitnessway.util.Nutrient.mapNutrients
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: IAuthRepository,
    private val nutrientRepo: INutrientRepository,
    private val foodRepo: IFoodRepository,
    private val managers: IProfileManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    IGoalsManager by managers.goals,
    IColorsManager by managers.colors {

    init {
        managers.goals.init(viewModelScope)
        managers.colors.init(viewModelScope)
    }

    val user = userStateHolder.userState.value.user

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val nutrientRepoUiState = nutrientRepo.uiState

    fun getNutrients() {
        nutrientRepo.loadNutrients()
    }

    fun setNutrientGoals() {
        val user = user ?: return

        // Get current goals data to update it optimistically
        val currentGoalsData = nutrientRepoUiState.value.nutrientsUiState

        // Only proceed if there are nutrient goals data
        if (currentGoalsData !is UiState.Success) return

        val modifiedGoals = managers.goals.modifiedGoals.value

        // Store updated nutrient goal data
        val optimisticNutrientData = currentGoalsData.data.mapNutrients { nutrients ->
            updateNutrientGoals(nutrients, modifiedGoals)
        }

        val request = NutrientGoalsPostRequest(
            userId = user.id,
            goals = modifiedGoals.map {
                NutrientIdWithGoal(
                    nutrientId = it.key,
                    goal = it.value.toDouble()
                )
            }
        )

        // Update UI immediately
        nutrientRepo.updateState {
            it.copy(nutrientsUiState = UiState.Success(optimisticNutrientData))
        }

        viewModelScope.launch {
            nutrientRepo.setNutrientGoals(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientGoalsSetUiState = state) }

                        nutrientRepo.clearNutrientIntakesUiCache()
                        foodRepo.refreshFoods()
                        foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(nutrientGoalsSetUiState = state)
                        }

                        nutrientRepo.updateState {
                            it.copy(nutrientsUiState = currentGoalsData)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun setNutrientColors() {
        val user = user ?: return

        // Get current nutrients UI data to update it optimistically
        val currentNutrientsUiState = nutrientRepoUiState.value.nutrientsUiState

        // Only proceed if there is UI data
        if (currentNutrientsUiState !is UiState.Success) return

        val modifiedColors = managers.colors.modifiedColors.value

        // Store updated nutrient colors data
        val optimisticNutrientsData = currentNutrientsUiState.data.mapNutrients {
            updateNutrientColors(it, modifiedColors)
        }

        val request = NutrientColorsPostRequest(
            userId = user.id,
            colors = modifiedColors.map {
                NutrientIdWithColor(
                    nutrientId = it.key,
                    hexColor = "#" + it.value
                )
            }
        )

        nutrientRepo.updateState {
            it.copy(nutrientsUiState = UiState.Success(optimisticNutrientsData))
        }

        viewModelScope.launch {
            nutrientRepo.setNutrientColors(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientColorsSetUiState = state) }
                    }

                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(nutrientGoalsSetUiState = state)
                        }

                        nutrientRepo.updateState {
                            it.copy(nutrientsUiState = currentNutrientsUiState)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout().collect { state ->
                _uiState.update { it.copy(logoutState = state) }
            }
        }
    }

    fun resetNutrientGoalsUpdateState() {
        _uiState.update {
            it.copy(nutrientGoalsSetUiState = UiState.Idle)
        }
    }
}

private fun updateNutrientGoals(
    nutrients: List<NutrientWithPreferences>,
    modifiedGoals: Map<Int, String>
): List<NutrientWithPreferences> {
    return updateNutrientPreferences(nutrients, modifiedGoals) { prefs, newGoal ->
        prefs.copy(goal = newGoal.toDouble())
    }
}

private fun updateNutrientColors(
    nutrients: List<NutrientWithPreferences>,
    modifiedColors: Map<Int, String>
): List<NutrientWithPreferences> {
    return updateNutrientPreferences(nutrients, modifiedColors) { prefs, newColor ->
        prefs.copy(hexColor = newColor)
    }
}

private fun updateNutrientPreferences(
    nutrients: List<NutrientWithPreferences>,
    modifiedValues: Map<Int, String>,
    propertyUpdater: (NutrientPreferences, String) -> NutrientPreferences
): List<NutrientWithPreferences> {
    return nutrients.map { nutrientData ->
        modifiedValues[nutrientData.nutrient.id]?.let { newValue ->
            nutrientData.copy(
                preferences = propertyUpdater(nutrientData.preferences, newValue)
            )
        } ?: nutrientData
    }
}