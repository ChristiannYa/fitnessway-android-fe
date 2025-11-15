package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.util.Nutrient.getNutrientColor

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
                        val color = getNutrientColor(field.name.nutrient.hexColor)

                        val dynamicColor = if (enabled) {
                            color ?: Color(0xFFFFFF)
                        } else MaterialTheme.colorScheme.primary

                        val brushGradient = if (enabled) {
                            Brush.linearGradient(
                                colors = listOf(
                                    dynamicColor,
                                    dynamicColor.copy(alpha = 0.9f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }

                        FoodCreationFormField(
                            field = field,
                            enabled = enabled,
                            labelStyle = TextStyle(
                                brush = brushGradient,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            )
                        )
                    }
                }
            )
        }
    )
}