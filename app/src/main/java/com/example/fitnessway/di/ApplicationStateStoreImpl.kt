package com.example.fitnessway.di

import com.example.fitnessway.data.state.IAuthStateHolder

class ApplicationStateStoreImpl(
   override val authStateHolder: IAuthStateHolder
) : IApplicationStateStore