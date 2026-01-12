package com.example.fitnessway.data.state.user

import com.example.fitnessway.data.model.MUser.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserStateHolderImpl : IUserStateHolder {
    private val _userState = MutableStateFlow(UserState())
    override val userState: StateFlow<UserState> = _userState

    override fun setUser(user: User) {
        _userState.value = UserState(user, isLoading = false)
    }

    override fun clearUser() {
        _userState.value = UserState(isLoading = false)
    }
}