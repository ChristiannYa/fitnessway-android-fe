package com.example.fitnessway.util.edible.creation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.form.field.FoodCreationNutrientField
import com.example.fitnessway.util.nutrient.INutrientDvControls

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    nutrientDvControls: INutrientDvControls.NutrientDvControls? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        fields.forEach { FoodCreationFormField(it, nutrientDvControls) }
    }
}