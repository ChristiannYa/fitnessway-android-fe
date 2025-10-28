package com.example.fitnessway.di

import com.example.fitnessway.data.state.token.ITokensStateHolder

interface IApplicationStateStore {
   val authStateHolder: ITokensStateHolder
}