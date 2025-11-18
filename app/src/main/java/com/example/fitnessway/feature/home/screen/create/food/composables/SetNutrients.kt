package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodCreationNutrientField

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    isPremiumUser: Boolean,
) {
    Surface(
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    fields.forEach { field ->
                        val enabled = (!field.name.nutrient.isPremium || isPremiumUser)

                        FoodCreationFormField(
                            field = field,
                            enabled = enabled,
                            modifier = Modifier
                                .blur(if (enabled) 0.dp else 3.dp),
                        )
                    }
                }
            )
        }
    )
}