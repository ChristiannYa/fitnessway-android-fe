package com.example.fitnessway.data.model.form

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization

data class FormField<T : FormFieldName>(
    val name: T,
    val label: String,
    val value: String,
    val enabled: Boolean = true,
    val updateState: (String) -> Unit,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(),
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val autoCapitalize: KeyboardCapitalization = KeyboardCapitalization.None,
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
