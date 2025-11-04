package com.example.fitnessway.feature.welcome.screen.login.viewmodel

import android.os.Build
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.form.field.Rules.passwordRules
import com.example.fitnessway.util.form.field.rules.PasswordInlineRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
   private val repo: IAuthRepository
) : ViewModel() {
   private val _loginUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
   val loginUiState: StateFlow<UiState<Unit>> = _loginUiState

   // @NOTE
   // - `private set` is mainly for `mutableStateOf`
   // - `derivedStateOf` is read-only. It is computed from other
   //    state, so there is no need to make it private

   var email by mutableStateOf("")
      private set

   var password by mutableStateOf("")
      private set

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

      val result = PasswordInlineRules(password) checkWith passwordRules

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

   fun updateField(fieldName:  FormFieldName.Login, input: String) {
      // Clear error state when user starts typing
      if (_loginUiState.value is UiState.Error) {
         _loginUiState.value = UiState.Idle
      }

      when (fieldName) {
         FormFieldName.Login.EMAIL -> email = input
         FormFieldName.Login.PASSWORD -> password = input
      }
   }

   fun login() {
      if (!isFormValid) return

      viewModelScope.launch {
         val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

         repo.login(email, password, deviceName).collect { state ->
            _loginUiState.value = state
         }
      }
   }

   fun resetLoginState() {
      _loginUiState.value = UiState.Idle
   }
}