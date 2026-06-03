package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.mappers.mapnbt
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientPreferences
import com.example.fitnessway.data.model.m_26.UserTimezoneSetRequest
import com.example.fitnessway.data.repository.RepositoryOperations
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.edible_list.food.IUserFoodRepository
import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient_intakes.INutrientIntakesRepository
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.timezone.ITimezoneStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.colors.IColorsManager
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.date_time.IAppDateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId

class ProfileViewModel(
    private val authRepo: IAuthRepository,
    private val userRepo: IUserRepository,
    private val nutrientRepo: INutrientRepository,
    private val nutrientIntakesRepo: INutrientIntakesRepository,
    private val userFoodRepo: IUserFoodRepository,
    private val edibleLogRepo: IEdibleLogRepository,
    private val repoOperations: RepositoryOperations,
    private val managers: IProfileManagers,
    private val timezoneStateHolder: ITimezoneStateHolder,
    val dateTimeFormatter: IAppDateTimeFormatter,
) : ViewModel(),
    IGoalsManager by managers.goals,
    IColorsManager by managers.colors {

    init {
        managers.goals.init(viewModelScope)
        managers.colors.init(viewModelScope)
    }

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val userRepoUiState = userRepo.uiState
    val nutrientRepoUiState = nutrientRepo.uiState

    fun getNutrients() = nutrientRepo.load()
    fun refreshNutrients() = nutrientRepo.refresh()

    fun setNutrientGoals() {
        // Get current goals data to update it optimistically
        val currentNutrientsData = nutrientRepoUiState.value.nutrients

        // Only proceed if there are nutrient goals data
        if (currentNutrientsData !is UiState.Success) return

        val currentNutrients = currentNutrientsData.data

        val modifiedGoals = managers.goals.modifiedGoals.value

        // Store updated nutrient goal data to avoid refreshing nutrients
        val optimisticNutrientData = currentNutrients.mapnbt { _, nutrients ->
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
            nutrientRepo.setGoals(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientGoalsSetUiState = state) }

                        // Update UI immediately
                        managers.goals.initNutrientGoalsForm(optimisticNutrientData)

                        nutrientRepo.update {
                            it.copy(nutrients = UiState.Success(optimisticNutrientData))
                        }

                        nutrientIntakesRepo.clear()
                        userFoodRepo.refresh()
                        edibleLogRepo.clear()
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
        val currentNutrientsData = nutrientRepoUiState.value.nutrients

        // Only proceed if there is UI data
        if (currentNutrientsData !is UiState.Success) return

        val currentNutrients = currentNutrientsData.data
        val modifiedColors = managers.colors.modifiedColors.value

        // Store updated nutrient colors data
        val optimisticNutrientsData = currentNutrients.mapnbt { _, nutrients ->
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
            nutrientRepo.setColors(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientColorsSetUiState = state) }

                        // Update UI immediately
                        managers.colors.initNutrientColorsForm(optimisticNutrientsData)

                        nutrientRepo.update {
                            it.copy(nutrients = UiState.Success(optimisticNutrientsData))
                        }

                        nutrientIntakesRepo.clear()
                        userFoodRepo.refresh()
                        edibleLogRepo.clear()
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

    fun setUserTimezone(timezone: String) {
        val originalUser = userRepoUiState.value.userUiState.toSuccessOrNull() ?: return

        viewModelScope.launch {
            userRepo
                .setTimezone(UserTimezoneSetRequest(timezone))
                .collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            _uiState.update { it.copy(userTimezoneSetUiState = state) }

                            repoOperations.onTimezoneChange()
                            userRepo.update { it.copy(userUiState = UiState.Success(originalUser.copy(timezone = timezone))) }
                            timezoneStateHolder.setTimezone(ZoneId.of(timezone))
                        }

                        is UiState.Loading -> {
                            _uiState.update { it.copy(userTimezoneSetUiState = state) }
                        }

                        is UiState.Error -> {
                            _uiState.update { it.copy(userTimezoneSetUiState = state) }
                        }

                        else -> {}
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repoOperations.onLogout()

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

    fun resetNutrientColorsUpdateState() {
        _uiState.update {
            it.copy(nutrientColorsSetUiState = UiState.Idle)
        }
    }
}

private fun updateNutrientGoals(
    nutrients: List<NutrientData>,
    modifiedGoals: Map<Int, String>
): List<NutrientData> =
    updateNutrientPreferences(nutrients, modifiedGoals) { prefs, newGoal ->
        prefs.copy(goal = newGoal.toDouble())
    }

private fun updateNutrientColors(
    nutrients: List<NutrientData>,
    modifiedColors: Map<Int, String>
): List<NutrientData> =
    updateNutrientPreferences(nutrients, modifiedColors) { prefs, newColor ->
        prefs.copy(hexColor = "#$newColor")
    }

private fun updateNutrientPreferences(
    nutrients: List<NutrientData>,
    modifiedValues: Map<Int, String>,
    propertyUpdater: (NutrientPreferences, String) -> NutrientPreferences
): List<NutrientData> =
    nutrients.map { nutrientData ->
        modifiedValues[nutrientData.base.id]?.let { newValue ->
            nutrientData.copy(
                preferences = propertyUpdater(nutrientData.preferences, newValue)
            )
        } ?: nutrientData
    }