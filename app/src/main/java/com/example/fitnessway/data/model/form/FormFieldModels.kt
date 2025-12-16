package com.example.fitnessway.data.model.form

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

data class FormField<T : FormFieldName>(
    val name: T,
    val label: String,
    val value: String,
    val updateState: (String) -> Unit,
    val keyboardType: KeyboardType = KeyboardType.Text,
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
