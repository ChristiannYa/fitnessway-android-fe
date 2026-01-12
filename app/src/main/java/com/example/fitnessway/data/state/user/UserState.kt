package com.example.fitnessway.data.state.user

import com.example.fitnessway.data.model.MUser.Model.User

data class UserState(
    val user: User? = null,

    // `isLoading` is set to false by default because the user's data is
    // not obtained on app launch, compared to tokens
    val isLoading: Boolean = false
)