package com.example.fitnessway.feature.profile.screen.goals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.getNutrientColor

@Composable
fun NutrientGoalsContent(nutrients: NutrientsByType<NutrientApiFormat>) {
    val nutrientsByType = NutrientType.entries.associateWith { type ->
        filterNutrientsByType(nutrients = nutrients, type = type)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            nutrientsByType.forEach { (type, nutrients) ->
                Box(
                    modifier = Modifier.areaContainerLarge(
                        shape = RoundedCornerShape(10.dp)
                    ),
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            content = {
                                val title = when (type) {
                                    NutrientType.BASIC -> "Base nutrients"
                                    else -> "${type.name.lowercase().replaceFirstChar { it.uppercase() }}s"
                                }

                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    content = {
                                        nutrients.forEach { (nutrient, goal) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                content = {
                                                    Text(
                                                        text = buildAnnotatedString {
                                                            withStyle(
                                                                style = SpanStyle(
                                                                    fontWeight = FontWeight.Medium
                                                                ),
                                                                block = { append("${nutrient.name} ") }
                                                            )

                                                            if (type == NutrientType.MINERAL) {
                                                                append(text = "(${nutrient.symbol}) ")
                                                            }

                                                            withStyle(
                                                                style = SpanStyle(
                                                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                                                        0.6f
                                                                    )
                                                                ),
                                                                block = { append(text = nutrient.unit) }
                                                            )
                                                        },
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )

                                                    val goalText = if (goal != null) {
                                                        doubleFormatter(goal, 2)
                                                    } else ""

                                                    val goalTextColor = getNutrientColor(nutrient.hexColor) ?: MaterialTheme.colorScheme.primary

                                                    Text(
                                                        text = goalText,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontFamily = FontFamily.Default,
                                                        color = goalTextColor
                                                    )
                                                }
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