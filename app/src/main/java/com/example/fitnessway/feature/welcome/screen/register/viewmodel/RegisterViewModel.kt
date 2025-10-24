package com.example.fitnessway.feature.welcome.screen.register.viewmodel

import android.os.Build
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.welcome.Password
import com.example.fitnessway.data.model.welcome.passwordRules
import com.example.fitnessway.data.model.welcome.register.Name
import com.example.fitnessway.data.repository.IAuthRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val nameRules = listOf(
   Name::notEmptyRule,
   Name::lengthRule,
   Name::validCharactersRule,
   Name::validFormatRule
)

class RegisterViewModel(
   private val repo: IAuthRepository
) : ViewModel() {
   private val _registerUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
   val registerUiState: StateFlow<UiState<Unit>> = _registerUiState

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

   // Step 1 validation
   val stepOneIsValid by derivedStateOf {
      name.isNotEmpty() && nameError == null
   }

   // Step 2 validation
   val stepTwoIsValid by derivedStateOf {
      email.isNotEmpty() && emailError == null
   }

   // Step 3 validation
   val stepThreeIsValid by derivedStateOf {
      password.isNotEmpty() && passwordError == null
         && confirmPassword.isNotEmpty() && confirmPasswordError == null
   }

   val isCurrentStepValid by derivedStateOf {
      when (currentStep) {
         1 -> stepOneIsValid
         2 -> stepTwoIsValid
         3 -> stepThreeIsValid
         else -> false
      }
   }

   val isRegistering by derivedStateOf {
      registerUiState.value is UiState.Loading
   }

   fun updateStep(
      step: Int,
      goesBack: Boolean = true,
      onRegisterComplete: (() -> Unit)? = null
   ) {
      when (step) {
         1 -> if (stepOneIsValid) currentStep = 2

         2 -> {
            if (stepTwoIsValid) currentStep = 3
            if (goesBack) currentStep = 1
         }

         3 -> {
            if (stepThreeIsValid) {
               register()
               onRegisterComplete?.invoke()
            }
            if (goesBack) currentStep = 2
         }
      }
   }

   fun register() {
      viewModelScope.launch {
         val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

         repo.register(
            name,
            email,
            password,
            confirmPassword,
            deviceName
         ).collect { state ->
            _registerUiState.value = state
         }
      }
   }

   fun resetRegisterState() {
      _registerUiState.value = UiState.Idle
   }
}