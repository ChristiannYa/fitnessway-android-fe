package com.example.fitnessway.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.Ui.NutrientsAsLine
import com.example.fitnessway.util.Nutrient.Ui.PagedNutrients
import com.example.fitnessway.util.Nutrient.getAllNutrients
import com.example.fitnessway.util.Nutrient.mapNutrients

object Food {
    fun subtractNutrientsFromIntakes(
        currentIntakes: NutrientIntakesByType,
        foodLog: FoodLogData
    ): NutrientIntakesByType {
        return currentIntakes.mapNutrients { intakes ->
            intakes.map { intake ->
                val foodNutrientAmountData = getAllNutrients(foodLog.food.nutrients).find {
                    val nutrient = it.nutrientWithPreferences.nutrient
                    nutrient.id == intake.nutrientWithPreferences.nutrient.id
                }

                if (foodNutrientAmountData != null) {
                    val newAmount = intake.amount - foodNutrientAmountData.amount
                    intake.copy(amount = newAmount)
                } else intake
            }
        }
    }

    /**
     * Food log data is not being asked for because the function would not be compatible for
     * when editing a food **to be** logged, not an **existing** one, hence just the current and
     * new serving sizes are asked for instead
     */
    fun calcNutrientsBasedOnFoodLogServings(
        nutrients: NutrientsByType<NutrientAmountData>,
        currentServings: Double,
        newServings: Double
    ): NutrientsByType<NutrientAmountData> {
        return nutrients.mapNutrients { nutrientsList ->
            nutrientsList.map { nutrientData ->
                val foodNutrientAmountData = getAllNutrients(nutrients).find {
                    val nutrient = it.nutrientWithPreferences.nutrient
                    nutrient.id == nutrientData.nutrientWithPreferences.nutrient.id
                }

                if (foodNutrientAmountData != null) {
                    val originalAmount = foodNutrientAmountData.amount / currentServings
                    val newAmount = (originalAmount) * newServings
                    nutrientData.copy(amount = newAmount)
                } else nutrientData
            }
        }
    }

    data class FoodComposables(
        val food: FoodInformation,
        val nutrients: NutrientsByType<NutrientAmountData> = food.nutrients,
        val user: User
    ) {
        @Composable
        fun BaseInformation(
            /**
             * Horizontal alignment in between the brand and name
             */
            topHorizontalAlignment: Alignment.Horizontal = Alignment.Start,

            /**
             * Horizontal alignment for the amount per serving
             */
            bottomHorizontalAlignment: Alignment.Horizontal = Alignment.End,


            /**
             * Vertical space between the brand, name, and amount per serving
             */
            verticalSpace: Dp = 0.dp,

            foodLogServings: Double = 1.0
        ) {
            Box(
                contentAlignment = Alignment.Center,
                content = {
                    Column(
                        horizontalAlignment = bottomHorizontalAlignment,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                        content = {
                            val asideColor = MaterialTheme.colorScheme.onBackground.copy(0.8f)

                            Column(
                                horizontalAlignment = topHorizontalAlignment,
                                verticalArrangement = Arrangement.spacedBy(verticalSpace),
                                content = {
                                    val foodBrand = food.information.brand?.ifEmpty { "~" } ?: "~"

                                    Text(
                                        text = foodBrand,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = asideColor
                                    )

                                    Text(
                                        text = food.information.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                content = {
                                    val amPerSer =
                                        doubleFormatter(food.information.amountPerServing * foodLogServings)

                                    Text(
                                        text = amPerSer,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Default,
                                        color = asideColor
                                    )
                                    Text(
                                        text = food.information.servingUnit,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = asideColor
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }

        @Composable
        fun NutrientSummary() {
            Column(
                modifier = Modifier.areaContainer(size = AreaContainerSize.MEDIUM),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    PagedNutrients(
                        nutrients = nutrients.basic,
                        displayFormat = Nutrient.ScrollableNutrientsFormat.CIRCLE,
                        isUserPremium = user.isPremium
                    )
                }
            )
        }

        @Composable
        fun RemainingNutrients() {
            val remainingNutrients = listOf(
                NutrientType.VITAMIN to nutrients.vitamin,
                NutrientType.MINERAL to nutrients.mineral
            )

            if (remainingNutrients.any { it.second.isNotEmpty() }) {
                Column(
                    modifier = Modifier.areaContainer(size = AreaContainerSize.MEDIUM),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        remainingNutrients.forEachIndexed { index, (type, ns) ->
                            val title = type.name.lowercase().replaceFirstChar { it.uppercase() }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                content = {
                                    if (ns.isNotEmpty()) {
                                        Text(
                                            text = "${title}s",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        NutrientsAsLine(ns)
                                    }
                                }
                            )

                            // Space between each nutrient category mini section
                            if (index < remainingNutrients.lastIndex && ns.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                )
            }
        }
    }
}