package com.example.fitnessway.feature.welcome.screen.register.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitnessway.data.model.welcome.FormFieldName
import com.example.fitnessway.data.model.welcome.Name
import com.example.fitnessway.data.model.welcome.Password
import com.example.fitnessway.data.model.welcome.passwordRules

private val nameRules = listOf(
   Name::notEmptyRule,
   Name::lengthRule,
   Name::validCharactersRule,
   Name::validFormatRule
)

class RegisterViewModel : ViewModel() {
   var currentStep by mutableIntStateOf(1)
      private set

   var name by mutableStateOf("")
      private set

   var email by mutableStateOf("")
      private set

   var password by mutableStateOf("")
      private set

   var confirmPassword by mutableStateOf("")
      private set

   fun updateField(fieldName: FormFieldName.Register, input: String) {
      when (fieldName) {
         FormFieldName.Register.NAME -> name = input
         FormFieldName.Register.EMAIL -> email = input
         FormFieldName.Register.PASSWORD -> password = input
         FormFieldName.Register.CONFIRM_PASSWORD -> confirmPassword = input
      }
   }

   fun updateCurrentStep(
      fieldError: String?,
      goNext: Boolean?,
      goBack: Boolean?
   ) {
      if (fieldError == null && goNext == true) currentStep++
      if (goBack == true && currentStep != 1) currentStep--
   }

   // Name validation - returns error message or null
   val nameError by derivedStateOf {
      if (name.isEmpty()) return@derivedStateOf null

      val result = Name(name.trim()) checkWith nameRules
      result.exceptionOrNull()?.message
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

   // Confirm password validation - returns error message or null
   val confirmPasswordError by derivedStateOf {
      if (confirmPassword.isEmpty()) return@derivedStateOf null

      if (password != confirmPassword) {
         "Passwords don't match"
      } else {
         null
      }
   }

   val isFormValid by derivedStateOf {
      name.isNotEmpty() &&
         email.isNotEmpty() &&
         password.isNotEmpty() &&
         confirmPassword.isNotEmpty() &&
         nameError == null &&
         emailError == null &&
         passwordError == null &&
         confirmPasswordError == null
   }
}