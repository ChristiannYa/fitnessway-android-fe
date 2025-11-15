package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
                            color ?: MaterialTheme.colorScheme.onBackground
                        } else MaterialTheme.colorScheme.primary

                        FoodCreationFormField(
                            field = field,
                            enabled = enabled,
                            labelColor = dynamicColor,
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = dynamicColor.copy(0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .blur(if (enabled) 0.dp else 2.dp),
                        )
                    }
                }
            )
        }
    )
}