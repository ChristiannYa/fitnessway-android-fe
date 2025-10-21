package com.example.fitnessway.di

import com.example.fitnessway.data.state.IAuthStateHolder

interface IApplicationStateStore {
   val authStateHolder: IAuthStateHolder
}