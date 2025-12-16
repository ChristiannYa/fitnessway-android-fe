package com.example.fitnessway.util.form.field.provider

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientColorUpdateField
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates

class NutrientColorsFieldsProvider(
    private val formState: FormState<FormStates.NutrientColors>,
    private val onFieldUpdate: (FormFieldName.NutrientColorUpdate, String) -> Unit
) {
    fun nutrientColor(nutrientData: NutrientWithPreferences): NutrientColorUpdateField {
        val nutrient = nutrientData.nutrient

        return NutrientColorUpdateField(
            name = FormFieldName.NutrientColorUpdate(nutrientData),
            label = "${nutrient.name} ${nutrient.unit}",
            value = formState.data.colors[nutrient.id] ?: "~",
            updateState = { value ->
                onFieldUpdate(
                    FormFieldName.NutrientColorUpdate(nutrientData),
                    value
                )
            }
        )
    }
}