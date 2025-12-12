package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall
import com.example.fitnessway.ui.theme.AppModifiers.blurPremiumItem

@Composable
fun SetNutrients(
    fields: List<FoodCreationNutrientField>,
    nutrientsWithoutGoal: List<Nutrient>,
    isPremiumUser: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            fields.forEach { field ->
                val nutrient = field.name.nutrientWithPreferences.nutrient
                val enabled = (!nutrient.isPremium || isPremiumUser)

                FoodCreationFormField(
                    field = field,
                    enabled = enabled,
                    modifier = Modifier
                        .blurPremiumItem(enabled),
                )
            }

            Box(
                modifier = Modifier
                    .areaContainerSmall(
                        areaColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            Text(
                                text = "These nutrients do not have a goal. Please provide them to be able to add them to your food",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                content = {
                                    nutrientsWithoutGoal.forEach {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}