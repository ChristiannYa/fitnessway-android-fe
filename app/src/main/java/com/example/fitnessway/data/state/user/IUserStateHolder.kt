package com.example.fitnessway.data.state.user

import com.example.fitnessway.data.model.user.User
import kotlinx.coroutines.flow.StateFlow

interface IUserStateHolder {
    val userState: StateFlow<UserState>

    fun setUser(user: User)

    fun clearUser()
}