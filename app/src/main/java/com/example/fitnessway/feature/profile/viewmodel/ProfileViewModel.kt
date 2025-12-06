package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.profile.manager.IProfileManagers
import com.example.fitnessway.feature.profile.manager.goals.IGoalsManager
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: IAuthRepository,
    private val nutrientRepo: INutrientRepository,
    private val managers: IProfileManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    IGoalsManager by managers.goals {
    val user = userStateHolder.userState.value.user

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun getNutrients() {
        viewModelScope.launch {
            nutrientRepo.getNutrients().collect { state ->
                _uiState.update { it.copy(nutrientsState = state) }

                if (state is UiState.Success) {
                    launch(Dispatchers.Default) {
                        managers.goals.initNutrientGoalsForm(
                            goalsData = state.data
                        )
                    }
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
}