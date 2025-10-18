package com.example.fitnessway.feature.welcome.screen.login.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.fitnessway.data.model.welcome.FormFieldName
import com.example.fitnessway.data.model.welcome.Password
import com.example.fitnessway.data.model.welcome.passwordRules

class LoginViewModel : ViewModel() {
   // - `private set` is mainly for `mutableStateOf`
   // - `derivedStateOf` is read-only. It is computed from other
   //    state, so there is no need to make it private

   var email by mutableStateOf("")
      private set

   var password by mutableStateOf("")
      private set

   fun updateField(fieldName:  FormFieldName.Login, input: String) {
      when (fieldName) {
         FormFieldName.Login.EMAIL -> email = input
         FormFieldName.Login.PASSWORD -> password = input
      }
   }

   // Email validation - returns error message or null
   val emailError by derivedStateOf {
      if (email.isEmpty()) return@derivedStateOf null

      if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
         "Invalid email format"
      } else {
         null
      }
   }

   // Password validation - returns error message or null
   val passwordError by derivedStateOf {
      if (password.isEmpty()) return@derivedStateOf null

      val result = Password(password) checkWith passwordRules

      if (result.isFailure) {
         result.exceptionOrNull()?.message
      } else {
         null
      }
   }

   val isFormValid by derivedStateOf {
      email.isNotEmpty() &&
         password.isNotEmpty() &&
         emailError == null &&
         passwordError == null
   }
}