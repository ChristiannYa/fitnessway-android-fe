package com.example.fitnessway.feature.home.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.Food
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.model.food.FoodNutrientAmountData
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.OrangeWarning
import com.example.fitnessway.ui.theme.WhiteBackground
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UiState

@Composable
fun FoodLogs(
    state: UiState<FoodLogsByCategory>,
    onFoodLog: () -> Unit
) {
    when (state) {
        is UiState.Loading -> Text("Loading food logs")
        is UiState.Success -> FoodLogsCategorized(state.data, onFoodLog)
        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}


@Composable
fun FoodLogsCategorized(
    foodLogs: FoodLogsByCategory,
    onFoodLog: () -> Unit,
) {
    Column(
        modifier = Modifier.areaContainerLarge(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = {
            FoodLogCategory(
                foodLogs = foodLogs.breakfast,
                category = "breakfast",
                onFoodLog
            )
            FoodLogCategory(
                foodLogs = foodLogs.lunch,
                category = "lunch",
                onFoodLog
            )
            FoodLogCategory(
                foodLogs = foodLogs.dinner,
                category = "dinner",
                onFoodLog
            )
            FoodLogCategory(
                foodLogs = foodLogs.supplement,
                category = "supplement",
                onFoodLog
            )
        }
    )
}

@Composable
fun FoodLogCategory(
    foodLogs: List<FoodLogData>,
    category: String,
    onFoodLog: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = {
            Text(
                text = category.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier.areaContainerMedium(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    if (foodLogs.isEmpty()) {
                        Text(
                            text = "No $category logged yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        foodLogs.forEach { log ->
                            FoodLog(log)
                        }
                    }

                    TextButton(
                        onClick = onFoodLog,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = WhiteBackground
                        ),
                        content = {
                            Text(
                                text = "Log",
                                fontFamily = robotoSerifFamily,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun FoodLog(foodLog: FoodLogData) {
    val food = foodLog.food.information

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        content = {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (foodLog.foodStatus != "present") {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = OrangeWarning,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    )
                    Text(
                        text = if (food.brand == null || food.brand == "") "~" else food.brand,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                    )

                    Text(
                        text = "${doubleFormatter(food.amountPerServing)} ${food.servingUnit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                    )
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = foodLog.time,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                modifier = Modifier.wrapContentWidth(unbounded = false)
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodLogsPreview() {
    FitnesswayTheme {
        FoodLogsCategorized(
            foodLogs = sampleFoodLogsByCategory,
            onFoodLog = {}
        )
    }
}

val sampleFoodLogsByCategory = FoodLogsByCategory(
    breakfast = listOf(
        FoodLogData(
            id = 1,
            category = "breakfast",
            time = "08:30 AM",
            servings = 1.0,
            foodStatus = "confirmed",
            foodSnapshotId = 12345,
            food = FoodInformation(
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
            )
        ),
        FoodLogData(
            id = 2,
            category = "breakfast",
            time = "08:45 AM",
            servings = 2.0,
            foodStatus = "confirmed",
            foodSnapshotId = 12346,
            food = FoodInformation(
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
                        )
                    ),
                    vitamin = listOf(),
                    mineral = listOf()
                )
            )
        )
    ),
    lunch = listOf(
        FoodLogData(
            id = 3,
            category = "lunch",
            time = "12:30 PM",
            servings = 1.0,
            foodStatus = "confirmed",
            foodSnapshotId = 0,
            food = FoodInformation(
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
                        )
                    ),
                    vitamin = listOf(),
                    mineral = listOf()
                )
            )
        )
    ),
    dinner = listOf(
        FoodLogData(
            id = 4,
            category = "dinner",
            time = "07:15 PM",
            servings = 1.5,
            foodStatus = "confirmed",
            foodSnapshotId = 0,
            food = FoodInformation(
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
                    vitamin = listOf(),
                    mineral = listOf()
                )
            )
        )
    ),
    supplement = listOf(
        FoodLogData(
            id = 5,
            category = "supplement",
            time = "09:00 AM",
            servings = 1.0,
            foodStatus = "confirmed",
            foodSnapshotId = 0,
            food = FoodInformation(
                information = Food(
                    id = 105,
                    name = "Multivitamin",
                    brand = "Centrum",
                    amountPerServing = 1.0,
                    servingUnit = "tablet"
                ),
                nutrients = NutrientsByType(
                    basic = listOf(),
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
                            amount = 20.0
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
                            amount = 6.0
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
    )
)

val sampleFoodLogData = FoodLogData(
    id = 4,
    category = "breakfast",
    time = "07:14 PM",
    servings = 1.4,
    foodStatus = "deleted",
    foodSnapshotId = 0,
    food = FoodInformation(
        information = Food(
            id = 18,
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
                )
            )
        )
    )
)