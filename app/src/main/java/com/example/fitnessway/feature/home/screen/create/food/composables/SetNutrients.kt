package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    nutrientsWithoutGoal: List<Nutrient>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            fields.forEach { field ->
                val nutrient = field.name.nutrientWithPreferences.nutrient

                FoodCreationFormField(field)
            }

            if (nutrientsWithoutGoal.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .areaContainerSmall(),
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                val messageText = if (nutrientsWithoutGoal.size > 2) {
                                    "These nutrients are missing goals. If you choose to set goals for them, " +
                                            "they can be added to your food."
                                } else "This nutrient is missing a goal. If you choose to set a goal for it, " +
                                        "it can be added to your food."

                                Text(
                                    text = messageText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    content = {
                                        nutrientsWithoutGoal.forEach {
                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    )
}