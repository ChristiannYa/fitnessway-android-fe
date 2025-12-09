package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: IAuthRepository,
    private val nutrientRepo: INutrientRepository,
    private val managers: IProfileManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    IGoalsManager by managers.goals {

    init {
        managers.goals.init(viewModelScope)
    }

    val user = userStateHolder.userState.value.user

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun getNutrients() {
        viewModelScope.launch {
            nutrientRepo.getNutrients().collect { state ->
                _uiState.update { it.copy(nutrientsState = state) }
            }
        }
    }

    fun setNutrientGoals() {
        val user = user ?: return

        // Get current goals data to update it optimistically
        val currentGoalsData = _uiState.value.nutrientsState

        // Only proceed if there are nutrient goals data
        if (currentGoalsData !is UiState.Success) return

        val modifiedGoals = managers.goals.modifiedGoals.value

        // Store updated nutrient goal data
        val optimisticNutrientData = NutrientsByType(
            basic = updateNutrientGoals(currentGoalsData.data.basic, modifiedGoals),
            vitamin = updateNutrientGoals(currentGoalsData.data.vitamin, modifiedGoals),
            mineral = updateNutrientGoals(currentGoalsData.data.mineral, modifiedGoals)
        )

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
        _uiState.update {
            it.copy(
                nutrientsState = UiState.Success(optimisticNutrientData)
            )
        }

        viewModelScope.launch {
            nutrientRepo.setNutrientGoals(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(nutrientGoalsPostState = state) }
                    }

                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(
                                nutrientsState = currentGoalsData,
                                nutrientGoalsPostState = state
                            )
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
        _uiState.update { it.copy(nutrientGoalsPostState = UiState.Idle) }
    }
}

private fun updateNutrientGoals(
    nutrients: List<NutrientApiFormat>,
    modifiedGoals: Map<Int, String>
): List<NutrientApiFormat> {
    return nutrients.map { nutrientData ->
        // Only update if this nutrient was modified
        modifiedGoals[nutrientData.nutrient.id]?.let { newGoal ->
            nutrientData.copy(
                goal = newGoal.toDouble()
            )
        } ?: nutrientData
    }
}