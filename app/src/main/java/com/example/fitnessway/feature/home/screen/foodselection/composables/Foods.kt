package com.example.fitnessway.feature.home.screen.foodselection.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.Food
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodNutrientAmountData
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UiState

@Composable
fun Foods(
    state: UiState<List<FoodInformation>>,
) {
    when (state) {
        is UiState.Loading -> Text("Loading foods")
        is UiState.Success -> Foods(state.data)
        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
fun Foods(foods: List<FoodInformation>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            foods.forEach {
                item { Food(it) }
            }
        }
    )
}

@Composable
fun Food(food: FoodInformation) {
    val missingBrand = food.information.brand == null || food.information.brand == ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .areaContainerSmall(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            Text(
                text = food.information.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (missingBrand) "~" else food.information.brand,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    food.nutrients.basic.forEach { (_, amount) ->
                        Text(
                            text = doubleFormatter(amount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodsPreview() {
    FitnesswayTheme {
        Foods(mockFoodInformationList)
    }
}

/*
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodPreview() {
    FitnesswayTheme {
        Food(mockFoodInformation)
    }
}

 */

val mockFoodInformationList = listOf(
    // Whole Milk
    FoodInformation(
        information = Food(
            id = 101,
            name = "Whole Milk",
            brand = "Organic Valley",
            amountPerServing = 244.0,
            servingUnit = "ml"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 149.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 2,
                        name = "Protein",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 7.7
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 3,
                        name = "Total Fat",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 8.0
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 10,
                        name = "Vitamin D",
                        symbol = "D",
                        unit = "mcg",
                        type = NutrientType.VITAMIN,
                        isPremium = false
                    ),
                    amount = 2.9
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 20,
                        name = "Calcium",
                        symbol = "Ca",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 276.0
                )
            )
        )
    ),

    // Scrambled Eggs
    FoodInformation(
        information = Food(
            id = 102,
            name = "Scrambled Eggs",
            brand = null,
            amountPerServing = 1.0,
            servingUnit = "egg"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 91.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 2,
                        name = "Protein",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 6.2
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 3,
                        name = "Total Fat",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 6.8
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 11,
                        name = "Vitamin B12",
                        symbol = "B12",
                        unit = "mcg",
                        type = NutrientType.VITAMIN,
                        isPremium = true
                    ),
                    amount = 0.6
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 22,
                        name = "Sodium",
                        symbol = "Na",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 171.0
                )
            )
        )
    ),

    // Grilled Chicken Breast
    FoodInformation(
        information = Food(
            id = 103,
            name = "Grilled Chicken Breast",
            brand = null,
            amountPerServing = 100.0,
            servingUnit = "g"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 165.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 2,
                        name = "Protein",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 31.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 3,
                        name = "Total Fat",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 3.6
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 13,
                        name = "Niacin",
                        symbol = "B3",
                        unit = "mg",
                        type = NutrientType.VITAMIN,
                        isPremium = true
                    ),
                    amount = 13.7
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 21,
                        name = "Potassium",
                        symbol = "K",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 256.0
                )
            )
        )
    ),

    // Brown Rice
    FoodInformation(
        information = Food(
            id = 104,
            name = "Brown Rice",
            brand = "Uncle Ben's",
            amountPerServing = 100.0,
            servingUnit = "g"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 112.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 2,
                        name = "Protein",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 2.6
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 4,
                        name = "Carbohydrates",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 23.5
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 14,
                        name = "Thiamin",
                        symbol = "B1",
                        unit = "mg",
                        type = NutrientType.VITAMIN,
                        isPremium = true
                    ),
                    amount = 0.2
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 23,
                        name = "Magnesium",
                        symbol = "Mg",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 43.0
                )
            )
        )
    ),

    // Banana
    FoodInformation(
        information = Food(
            id = 105,
            name = "Banana",
            brand = null,
            amountPerServing = 1.0,
            servingUnit = "medium"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 105.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 4,
                        name = "Carbohydrates",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 27.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 5,
                        name = "Sugar",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 14.4
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 15,
                        name = "Vitamin C",
                        symbol = "C",
                        unit = "mg",
                        type = NutrientType.VITAMIN,
                        isPremium = false
                    ),
                    amount = 10.3
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 21,
                        name = "Potassium",
                        symbol = "K",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 422.0
                )
            )
        )
    ),

    // Greek Yogurt
    FoodInformation(
        information = Food(
            id = 106,
            name = "Greek Yogurt",
            brand = "Fage",
            amountPerServing = 170.0,
            servingUnit = "g"
        ),
        nutrients = NutrientsByType(
            basic = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 1,
                        name = "Calories",
                        symbol = null,
                        unit = "kcal",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 100.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 2,
                        name = "Protein",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 17.0
                ),
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 3,
                        name = "Total Fat",
                        symbol = null,
                        unit = "g",
                        type = NutrientType.BASIC,
                        isPremium = false
                    ),
                    amount = 0.7
                )
            ),
            vitamin = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 11,
                        name = "Vitamin B12",
                        symbol = "B12",
                        unit = "mcg",
                        type = NutrientType.VITAMIN,
                        isPremium = true
                    ),
                    amount = 1.3
                )
            ),
            mineral = listOf(
                FoodNutrientAmountData(
                    nutrient = Nutrient(
                        id = 20,
                        name = "Calcium",
                        symbol = "Ca",
                        unit = "mg",
                        type = NutrientType.MINERAL,
                        isPremium = false
                    ),
                    amount = 200.0
                )
            )
        )
    )
)

val mockFoodInformation = FoodInformation(
    information = Food(
        id = 101,
        name = "Whole Milk",
        brand = "Organic Valley",
        amountPerServing = 244.0,
        servingUnit = "ml"
    ),
    nutrients = NutrientsByType(
        basic = listOf(
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 1,
                    name = "Calories",
                    symbol = null,
                    unit = "kcal",
                    type = NutrientType.BASIC,
                    isPremium = false
                ),
                amount = 149.0
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 2,
                    name = "Protein",
                    symbol = null,
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                ),
                amount = 7.7
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 3,
                    name = "Total Fat",
                    symbol = null,
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                ),
                amount = 8.0
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 4,
                    name = "Carbohydrates",
                    symbol = null,
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                ),
                amount = 11.7
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 5,
                    name = "Sugar",
                    symbol = null,
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                ),
                amount = 12.3
            )
        ),
        vitamin = listOf(
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 10,
                    name = "Vitamin D",
                    symbol = "D",
                    unit = "mcg",
                    type = NutrientType.VITAMIN,
                    isPremium = false
                ),
                amount = 2.9
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 11,
                    name = "Vitamin B12",
                    symbol = "B12",
                    unit = "mcg",
                    type = NutrientType.VITAMIN,
                    isPremium = true
                ),
                amount = 1.1
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 12,
                    name = "Vitamin A",
                    symbol = "A",
                    unit = "mcg",
                    type = NutrientType.VITAMIN,
                    isPremium = false
                ),
                amount = 112.0
            )
        ),
        mineral = listOf(
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 20,
                    name = "Calcium",
                    symbol = "Ca",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = false
                ),
                amount = 276.0
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 21,
                    name = "Potassium",
                    symbol = "K",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = false
                ),
                amount = 322.0
            ),
            FoodNutrientAmountData(
                nutrient = Nutrient(
                    id = 22,
                    name = "Sodium",
                    symbol = "Na",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = false
                ),
                amount = 105.0
            )
        )
    )
)