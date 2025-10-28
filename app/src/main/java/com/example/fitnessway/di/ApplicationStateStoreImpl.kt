package com.example.fitnessway.di

import com.example.fitnessway.data.state.auth.IAuthStateHolder

class ApplicationStateStoreImpl(
   override val authStateHolder: IAuthStateHolder
) : IApplicationStateStore