package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodNutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
import com.example.fitnessway.util.Formatters.doubleFormatter

@Composable
fun FoodInformation(food: FoodInformation) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            BaseInformation(food)
            NutrientsInformation(food.nutrients)
        }
    )
}

@Composable
fun BaseInformation(food: FoodInformation) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Column(
                horizontalAlignment = Alignment.End,
                content = {
                    Column(
                        content = {
                            Text(
                                text = food.information.brand ?: "~",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = food.information.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )

                    Row(
                        content = {
                            Text(
                                text = doubleFormatter(food.information.amountPerServing),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = food.information.servingUnit,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun NutrientsInformation(nutrients: NutrientsByType<FoodNutrientAmountData>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            NutrientSummary(nutrients)
            RemainingNutrients(nutrients)
        }
    )
}

@Composable
fun NutrientSummary(nutrients: NutrientsByType<FoodNutrientAmountData>) {
    Column(
        modifier = Modifier.areaContainerMedium(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    nutrients.basic.forEach { nutrientData ->
                        val targetProgress =
                            if (nutrientData.goal != null) {
                                ((nutrientData.amount / nutrientData.goal).toFloat())
                            } else {
                                0f
                            }

                        val animatedProgress by animateFloatAsState(
                            targetValue = targetProgress,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        )

                        val percentage = if (nutrientData.goal != null) {
                            ((nutrientData.amount / nutrientData.goal) * 100).toFloat()
                        } else {
                            0f
                        }

                        val color =
                            if (nutrientData.nutrient.hexColor != null) {
                                Color(nutrientData.nutrient.hexColor.toColorInt())
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            content = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(70.dp),
                                    content = {
                                        CircularProgressIndicator(
                                            progress = { animatedProgress },
                                            color = color,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            strokeCap = StrokeCap.Round,
                                            strokeWidth = 5.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = doubleFormatter(nutrientData.amount),
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    content = {
                                        Text(
                                            text = "${doubleFormatter(percentage.toDouble())}%",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = color.copy(0.8f),
                                        )
                                        Text(
                                            text = nutrientData.nutrient.name,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun RemainingNutrients(nutrients: NutrientsByType<FoodNutrientAmountData>) {
    val remainingNutrients = listOf(
        NutrientType.VITAMIN to nutrients.vitamin,
        NutrientType.MINERAL to nutrients.mineral
    )

    if (remainingNutrients.any { it.second.isNotEmpty() }) {
        Column(
            modifier = Modifier.areaContainerMedium(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                remainingNutrients.forEachIndexed { index, (type, ns) ->
                    val title = type.name.lowercase().replaceFirstChar { it.uppercase() }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            if (ns.isNotEmpty()) {
                                Text(
                                    text = "${title}s",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                ns.forEachIndexed { index, nutrientData ->
                                    val targetProgress =
                                        if (nutrientData.goal != null) {
                                            ((nutrientData.amount / nutrientData.goal).toFloat())
                                        } else {
                                            0f
                                        }

                                    val animatedProgress by animateFloatAsState(
                                        targetValue = targetProgress,
                                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                                    )

                                    val percentage = if (nutrientData.goal != null) {
                                        ((nutrientData.amount / nutrientData.goal) * 100).toFloat()
                                    } else {
                                        0f
                                    }

                                    val color =
                                        if (nutrientData.nutrient.hexColor != null) {
                                            Color(nutrientData.nutrient.hexColor.toColorInt())
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }

                                    val onBackground = MaterialTheme.colorScheme.onBackground

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        content = {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth(),
                                                content = {
                                                    // Left side: nutrient name and symbol
                                                    if (nutrientData.nutrient.type == NutrientType.MINERAL) {
                                                        Text(
                                                            text = buildAnnotatedString {
                                                                withStyle(style = SpanStyle(color = onBackground)) {
                                                                    append(nutrientData.nutrient.name)
                                                                }
                                                                withStyle(style = SpanStyle(color = onBackground)) {
                                                                    append(" ")
                                                                }
                                                                withStyle(
                                                                    style = SpanStyle(
                                                                        color = onBackground.copy(
                                                                            alpha = 0.5f
                                                                        )
                                                                    )
                                                                ) {
                                                                    append(nutrientData.nutrient.symbol)
                                                                }
                                                            },
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    } else {
                                                        Text(
                                                            text = nutrientData.nutrient.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }

                                                    // Right side: amount and unit
                                                    Text(
                                                        text = buildAnnotatedString {
                                                            withStyle(style = SpanStyle(color = onBackground)) {
                                                                append(doubleFormatter(nutrientData.amount))
                                                            }
                                                            append(" ")
                                                            withStyle(
                                                                style = SpanStyle(
                                                                    color = onBackground.copy(
                                                                        alpha = 0.5f
                                                                    )
                                                                )
                                                            ) {
                                                                append(nutrientData.nutrient.unit)
                                                            }
                                                        },
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth(),
                                                content = {
                                                    LinearProgressIndicator(
                                                        progress = { animatedProgress },
                                                        color = color,
                                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                                        strokeCap = StrokeCap.Round,
                                                        modifier = Modifier
                                                            .weight(1f),
                                                    )
                                                    Text(
                                                        text = "${doubleFormatter(percentage.toDouble())}%",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = color,
                                                        fontWeight = FontWeight.Medium,
                                                        textAlign = TextAlign.End,
                                                        modifier = Modifier.width(68.dp)
                                                    )
                                                }
                                            )
                                        }
                                    )

                                    if (index < ns.lastIndex && ns.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                        }
                    )

                    if (index < remainingNutrients.lastIndex && ns.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        )
    }
}