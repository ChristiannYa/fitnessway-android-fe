package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.ui.theme.AppModifiers.blurPremiumItem

@Composable
fun NutrientGoalsContent(
    nutrientFields: Map<NutrientType, List<NutrientGoalEditionField>>,
    user: User
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            nutrientFields.forEach { (type, goalFields) ->
                item(key = type) {
                    Box(
                        modifier = Modifier.areaContainerLarge(
                            shape = RoundedCornerShape(10.dp)
                        ),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                content = {
                                    val title = when (type) {
                                        NutrientType.BASIC -> "Nutrients"
                                        else -> "${
                                            type.name
                                                .lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        }s"
                                    }

                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        content = {
                                            goalFields.forEach { field ->
                                                val enabled =
                                                    (!field.name.nutrientData.nutrient.isPremium || user.isPremium)

                                                key(field.name.nutrientData.nutrient.id) {
                                                    GoalsEditionFormField(
                                                        field = field,
                                                        enabled = enabled,
                                                        modifier = Modifier.blurPremiumItem(enabled)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}