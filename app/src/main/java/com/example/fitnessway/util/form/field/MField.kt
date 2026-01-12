package com.example.fitnessway.util.form.field

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue

data class FormField<T : FormFieldName>(
    val name: T,
    val label: String,
    val value: String, // TODO: Remove this field (replaced by textFieldValue)
    val textFieldValue: TextFieldValue? = null,
    val enabled: Boolean = true,
    val updateState: (String) -> Unit, // TODO: Remove this field (replaced by updateTextFieldValueState)
    val updateTextFieldValueState: ((TextFieldValue) -> Unit)? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(),
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val autoCapitalize: KeyboardCapitalization = KeyboardCapitalization.None,
    val focusRequester: FocusRequester? = null,
    val errorMessage: String? = null
)

typealias LoginField = FormField<FormFieldName.Login>
typealias RegisterField = FormField<FormFieldName.Register>

typealias FoodLogField = FormField<FormFieldName.FoodLog>

typealias FoodLogEditionField = FormField<FormFieldName.FoodLogEdition>

typealias FoodCreationBaseField = FormField<FormFieldName.FoodCreation.BaseField>
typealias FoodCreationNutrientField = FormField<FormFieldName.FoodCreation.NutrientField>

typealias FoodEditionDetailField = FormField<FormFieldName.FoodEdition.DetailField>
typealias FoodEditionNutrientField = FormField<FormFieldName.FoodEdition.NutrientField>

typealias NutrientGoalEditionField = FormField<FormFieldName.NutrientGoalData>

typealias NutrientColorUpdateField = FormField<FormFieldName.NutrientColorUpdate>