package com.example.fitnessway.feature.lists.screen.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitnessway.util.INutrientDvControls
import com.example.fitnessway.util.UNutrient.hasDailyValue

@Composable
fun NutrientDvTrailingIcon(
    nutrientDvState: INutrientDvControls.NutrientDvState,
    fieldValue: String,
    modifier: Modifier = Modifier
): (@Composable () -> Unit)? {
    return if (nutrientDvState.nutrient != null && nutrientDvState.nutrient.hasDailyValue) {
        {
            NutrientDvToggleButton(
                isInNutrientDvMap = nutrientDvState.isInNutrientDvMap,
                enabled = fieldValue.isNotEmpty() || nutrientDvState.isInNutrientDvMap,
                onToggle = { shouldBeInMap ->
                    if (shouldBeInMap) {
                        nutrientDvState.nutrientDvControls?.onAdd?.invoke(
                            nutrientDvState.nutrient.id,
                            fieldValue
                        )
                    } else {
                        nutrientDvState.nutrientDvControls?.onRemove?.invoke(
                            nutrientDvState.nutrient.id
                        )
                    }
                },
                modifier = modifier
            )
        }
    } else null
}