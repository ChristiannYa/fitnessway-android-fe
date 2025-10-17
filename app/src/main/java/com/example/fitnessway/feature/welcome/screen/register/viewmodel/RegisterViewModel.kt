package com.example.fitnessway.feature.welcome.screen.register.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class RegisterViewModel : ViewModel() {
   var name by mutableStateOf("")
      private set

   var email by mutableStateOf("")
      private set

   var password by mutableStateOf("")
      private set

   var confirmPassword by mutableStateOf("")
      private set
}