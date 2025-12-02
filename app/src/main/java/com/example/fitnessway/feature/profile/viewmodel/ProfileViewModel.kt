package com.example.fitnessway.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitnessway.data.state.user.IUserStateHolder

class ProfileViewModel(
    userStateHolder: IUserStateHolder
) : ViewModel() {
    val user = userStateHolder.userState.value.user
}