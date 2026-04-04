package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.model.MNutrient.Model.NutrientPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.user_food.IFoodRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.colors.IColorsManager
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import com.example.fitnessway.util.UNutrient.mapNutrients
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.date_time.IAppDateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: IAuthRepository,
    private val nutrientRepo: INutrientRepository,
    private val foodRepo: IFoodRepository,
    private val managers: IProfileManagers,
    val dateTimeFormatter: IAppDateTimeFormatter,
    userStateHolder: IUserStateHolder,
) : ViewModel(),
    IGoalsManager by managers.goals,
    IColorsManager by managers.colors {

    init {
        managers.goals.init(viewModelScope)
        managers.colors.init(viewModelScope)
    }

    // @NOTE:
    // .map() returns a cold `Flow`. `stateIn()` converts it to a hot `StateFlow`
    // This creates a reactive reference to the user value from userStateHolder
    // that updates automatically when the user changes.
    val userFlow: StateFlow<MUser.Model.User?> = userStateHolder.userState
        .map { it.user }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val nutrientRepoUiState = nutrientRepo.uiState

    fun refreshNutrients() {
        nutrientRepo.refreshNutrients()
    }

    fun getNutrients() {
        nutrientRepo.loadNutrients()
    }

    fun setNutrientGoals() {
        // Get current goals data to update it optimistically
        val currentNutrientsData = nutrientRepoUiState.value.nutrientsUiState

        // Only proceed if there are nutrient goals data
        if (currentNutrientsData !is UiState.Success) return

        val currentNutrients = currentNutrientsData.data

        val modifiedGoals = managers.goals.modifiedGoals.value

        // Store updated nutrient goal data to avoid refreshing nutrients
        val optimisticNutrientData = currentNutrients.mapNutrients { _, nutrients ->
            updateNutrientGoals(nutrients, modifiedGoals)
        }

        val request = NutrientGoalsPostRequest(
            goals = modifiedGoals.map {
                NutrientIdWithGoal(
                    nutrientId = it.key,
                    goal = it.value.toDouble()
                )
            }
        )

        viewModelScope.launch {
            nutrientRepo.setNutrientGoals(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientGoalsSetUiState = state) }

                        // Update UI immediately
                        managers.goals.initNutrientGoalsForm(optimisticNutrientData)

                        nutrientRepo.updateState {
                            it.copy(nutrientsUiState = UiState.Success(optimisticNutrientData))
                        }

                        nutrientRepo.clearNutrientIntakesUiCache()
                        foodRepo.refreshFoods()
                        foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Loading -> {
                        _uiState.update { it.copy(nutrientGoalsSetUiState = state) }
                    }

                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(nutrientGoalsSetUiState = state)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun setNutrientColors() {
        // Get current nutrients UI data to update it optimistically
        val currentNutrientsData = nutrientRepoUiState.value.nutrientsUiState

        // Only proceed if there is UI data
        if (currentNutrientsData !is UiState.Success) return

        val currentNutrients = currentNutrientsData.data
        val modifiedColors = managers.colors.modifiedColors.value

        // Store updated nutrient colors data
        val optimisticNutrientsData = currentNutrients
            .mapNutrients { _, nutrients ->
                updateNutrientColors(nutrients, modifiedColors)
            }

        val request = NutrientColorsPostRequest(
            colors = modifiedColors.map {
                NutrientIdWithColor(
                    nutrientId = it.key,
                    hexColor = "#" + it.value
                )
            }
        )

        viewModelScope.launch {
            nutrientRepo.setNutrientColors(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientColorsSetUiState = state) }

                        // Update UI immediately
                        managers.colors.initNutrientColorsForm(optimisticNutrientsData)

                        nutrientRepo.updateState {
                            it.copy(nutrientsUiState = UiState.Success(optimisticNutrientsData))
                        }

                        nutrientRepo.clearNutrientIntakesUiCache()
                        foodRepo.refreshFoods()
                        foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Loading -> {
                        _uiState.update { it.copy(nutrientColorsSetUiState = state) }
                    }

                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(nutrientColorsSetUiState = state)
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
                nutrientRepo.clearRepository()
                foodRepo.clearRepository()

                _uiState.update { it.copy(logoutState = state) }
            }
        }
    }

    fun resetNutrientGoalsUpdateState() {
        _uiState.update {
            it.copy(nutrientGoalsSetUiState = UiState.Idle)
        }
    }

    fun resetNutrientColorsUpdateState() {
        _uiState.update {
            it.copy(nutrientColorsSetUiState = UiState.Idle)
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
        prefs.copy(hexColor = "#$newColor")
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