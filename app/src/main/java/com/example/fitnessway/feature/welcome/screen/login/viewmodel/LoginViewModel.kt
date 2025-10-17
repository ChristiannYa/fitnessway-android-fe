package com.example.fitnessway.feature.welcome.screen.login.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.fitnessway.data.model.welcome.FormFieldName

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

   val emailHasErrors by derivedStateOf {
      // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
      // Only triggers once the email has content
      email.isNotEmpty() &&
         !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
   }

   val passwordHasErrors by derivedStateOf {
      // Error when shorter than 8
      password.isNotEmpty() && password.length < 8
   }

   val isFormValid by derivedStateOf {
      email.isNotEmpty() &&
         password.isNotEmpty() &&
         !emailHasErrors &&
         !passwordHasErrors
   }
}