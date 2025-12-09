package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientIdWithGoal
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
        val goals = managers.goals.modifiedGoals.value

        val request = NutrientGoalsPostRequest(
            userId = user.id,
            goals = goals.map {
                NutrientIdWithGoal(
                    nutrientId = it.key,
                    goal = it.value.toDouble()
                )
            }
        )

        viewModelScope.launch {
            nutrientRepo.setNutrientGoals(request).collect { state ->
                _uiState.update { it.copy(nutrientGoalsPostState = state) }
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
}