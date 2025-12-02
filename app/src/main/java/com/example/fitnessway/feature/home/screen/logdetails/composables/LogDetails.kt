package com.example.fitnessway.feature.home.screen.logdetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.util.Food.FoodComposables

@Composable
fun LogDetails(
    foodLog: FoodLogData,
    isBlurredOverlayVisible: Boolean,
    nutrients: NutrientsByType<NutrientAmountData>
) {
    val foodComposables = remember(
        key1 = foodLog.food,
        key2 = nutrients
    ) { FoodComposables(foodLog.food, nutrients) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        content = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    val verticalSpace = 4.dp

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(verticalSpace),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                foodComposables.BaseInformation(
                                    topHorizontalAlignment = Alignment.CenterHorizontally,
                                    bottomHorizontalAlignment = Alignment.CenterHorizontally,
                                    verticalSpace = verticalSpace,
                                    foodLogServings = foodLog.servings
                                )

                                CatAndTime(foodLog)
                            }
                        )
                    }

                    item {
                        foodComposables.NutrientSummary()
                    }

                    item {
                        foodComposables.RemainingNutrients()
                    }
                }
            )

            BlurOverlay(
                isVisible = isBlurredOverlayVisible
            )
        }
    )
}

@Composable
fun CatAndTime(foodLog: FoodLogData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val category = foodLog.category.replaceFirstChar { it.uppercase() }

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        ),
                        block = {
                            append(category)
                        }
                    )
                    append(
                        text = " at "
                    )
                    append(text = foodLog.time)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}