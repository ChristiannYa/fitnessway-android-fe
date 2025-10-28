package com.example.fitnessway.di

import com.example.fitnessway.data.state.token.ITokensStateHolder

class ApplicationStateStoreImpl(
   override val authStateHolder: ITokensStateHolder
) : IApplicationStateStore