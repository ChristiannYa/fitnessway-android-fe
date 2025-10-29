package com.example.fitnessway.di

import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder

interface IApplicationStateStore {
   val authStateHolder: ITokensStateHolder
   val userStateHolder: IUserStateHolder
}