package com.example.fitnessway.feature.lists.screen.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.util.INutrientDvControls
import com.example.fitnessway.util.UNutrient.hasDailyValue
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName

@Composable
fun <T : FormFieldName> rememberNutrientDvState(
    field: FormField<T>,
    nutrientDvControls: INutrientDvControls.NutrientDvControls?,
    getNutrient: (T) -> MNutrient.Model.Nutrient?
): INutrientDvControls.NutrientDvState {
    val nutrient = getNutrient(field.name)

    // `by` delegate isn't used here because `dvConfig` is nullable
    val nutrientDvMap = nutrientDvControls?.nutrientDvMap?.collectAsState()?.value

    val isInNutrientDvMap = nutrient?.let {
        nutrientDvMap?.containsKey(it.id) ?: false
    } ?: false

    // Auto-remove from `dvMap` if field input becomes empty
    if (nutrient != null &&
        nutrient.hasDailyValue &&
        field.textFieldValue != null
    ) {
        LaunchedEffect(field.textFieldValue.text, isInNutrientDvMap) {
            if (field.textFieldValue.text.isEmpty() && isInNutrientDvMap) {
                nutrientDvControls?.onRemove?.invoke(nutrient.id)
            }
        }
    }

    return INutrientDvControls.NutrientDvState(nutrient, isInNutrientDvMap, nutrientDvControls)
}