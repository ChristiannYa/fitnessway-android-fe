package com.example.fitnessway.di

import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder

class ApplicationStateStoreImpl(
   override val authStateHolder: ITokensStateHolder,
   override val userStateHolder: IUserStateHolder
) : IApplicationStateStore