package com.example.fitnessway.data.model.welcome

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

data class LoginField(
   val label: String,
   val value: String,
   val updateState: (String) -> Unit,
   val keyboardType: KeyboardType = KeyboardType.Text,
   val autoCapitalize: KeyboardCapitalization = KeyboardCapitalization.None,
   val isSecureTextEntry: Boolean = false,
   val errorMessage: String? = null,
)
