package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.util.UiState

data class UserRepositoryUiState(
    val userUiState: UiState<User> = UiState.Loading
)