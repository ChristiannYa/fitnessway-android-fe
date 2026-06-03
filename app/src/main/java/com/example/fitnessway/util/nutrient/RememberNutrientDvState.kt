package com.example.fitnessway.util.nutrient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.fitnessway.data.model.m_26.NutrientBase
import com.example.fitnessway.util.form.field.FormField
import com.example.fitnessway.util.form.field.FormFieldName

@Composable
fun <T : FormFieldName> rememberNutrientDvState(
    field: FormField<T>,
    nutrientDvControls: INutrientDvControls.NutrientDvControls?,
    getNutrient: (T) -> NutrientBase?
): INutrientDvControls.NutrientDvState {
    val nutrient = getNutrient(field.name)

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