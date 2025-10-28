package com.example.fitnessway.feature.profile.screen.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileSettingsScreenViewModel(
   private val repo: IAuthRepository
) : ViewModel() {
   private val _profileUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
   val profileUiState: StateFlow<UiState<Unit>> = _profileUiState

   fun logout() {
      viewModelScope.launch {
         repo.logout().collect { state ->
            _profileUiState.value = state
         }
      }
   }
}