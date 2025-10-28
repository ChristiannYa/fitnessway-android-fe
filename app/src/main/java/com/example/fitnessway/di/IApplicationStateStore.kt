package com.example.fitnessway.di

import com.example.fitnessway.data.state.auth.IAuthStateHolder

interface IApplicationStateStore {
   val authStateHolder: IAuthStateHolder
}