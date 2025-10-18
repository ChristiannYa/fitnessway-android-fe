package com.example.fitnessway.data.model.welcome

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

data class FormField<T: FormFieldName> (
   val name: T,
   val label: String,
   val value: String,
   val updateState: (String) -> Unit,
   val keyboardType: KeyboardType = KeyboardType.Text,
   val autoCapitalize: KeyboardCapitalization = KeyboardCapitalization.None,
   val errorMessage: String? = null,
)

typealias LoginField = FormField<FormFieldName.Login>
typealias RegisterField = FormField<FormFieldName.Register>


