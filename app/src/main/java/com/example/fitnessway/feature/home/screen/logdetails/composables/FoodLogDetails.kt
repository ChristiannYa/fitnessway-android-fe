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
import com.example.fitnessway.data.model.food.FoodLogFoodStatus
import com.example.fitnessway.data.model.form.FoodLogEditionField
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.OrangeWarning
import com.example.fitnessway.util.Food.FoodComposables
import com.example.fitnessway.util.Formatters.doubleFormatter

private const val deletedFoodMessage = "You have removed this food from your food list"
private const val updatedFoodMessage = "You have updated this food format"

@Composable
fun FoodLogDetails(
    foodLog: FoodLogData,
    isBlurredOverlayVisible: Boolean,
    nutrients: NutrientsByType<NutrientAmountData>,
    servingField: FoodLogEditionField,
    amountPerServingField: FoodLogEditionField,
    user: User
) {
    val foodComposables = remember(
        key1 = foodLog.food,
        key2 = nutrients
    ) { FoodComposables(foodLog.food, nutrients, user) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        content = {
            val (foodStatusMsg, foodStatusAccent) = when (foodLog.foodStatus) {
                FoodLogFoodStatus.DELETED -> deletedFoodMessage to OrangeWarning
                FoodLogFoodStatus.UPDATED -> updatedFoodMessage to MaterialTheme.colorScheme.secondary
                else -> "" to MaterialTheme.colorScheme.onSurfaceVariant
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            foodComposables.BaseInformation(
                                topHorizontalAlignment = Alignment.CenterHorizontally,
                                bottomHorizontalAlignment = Alignment.CenterHorizontally,
                                verticalSpace = 2.dp,
                                foodLogServings = null
                            )

                            val foodLogServings = foodLog.servings
                            val food = foodLog.food.information
                            val foodAmountPerServing = doubleFormatter(food.amountPerServing)
                            val foodServingUnit = food.servingUnit

                            Box(
                                modifier = Modifier.areaContainer(
                                    size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                                    hugsContent = true
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FoodLogEditionField(servingField)

                                    Text(
                                        text = buildAnnotatedString {
                                            append("${if (foodLogServings == 1.0) "serving" else "servings"} of ")

                                            withStyle(
                                                style = SpanStyle(
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            ) {
                                                append("$foodAmountPerServing ")
                                            }

                                            append("$foodServingUnit =")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                                    )

                                    FoodLogEditionField(amountPerServingField)

                                    Text(
                                        text = "g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        foodComposables.NutrientSummary()
                    }

                    item {
                        foodComposables.RemainingNutrients()
                    }

                    if (foodLog.foodStatus != FoodLogFoodStatus.PRESENT) {
                        item {
                            Text(
                                text = foodStatusMsg,
                                style = MaterialTheme.typography.labelLarge,
                                color = foodStatusAccent
                            )
                        }
                    }


                }
            )

            BlurOverlay(isVisible = isBlurredOverlayVisible)
        }
    )
}