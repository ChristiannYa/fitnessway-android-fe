package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerSmall
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.Nutrient.getNutrientColor

@Composable
fun Foods(
    state: UiState<List<FoodInformation>>,
    setSelectedFoodToLog: (FoodInformation) -> Unit,
    onSelectedFoodToLog: () -> Unit
) {
    when (state) {
        is UiState.Loading -> {
            TextWithLoadingIndicator(
                loadingText = "Loading Foods"
            )
        }
        is UiState.Success -> {
            Foods(state.data, setSelectedFoodToLog, onSelectedFoodToLog)
        }
        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
fun Foods(
    foods: List<FoodInformation>,
    setSelectedFoodToLog: (FoodInformation) -> Unit,
    onSelectedFoodToLog: () -> Unit
) {
    if (foods.isEmpty()) {
        NotFoundText("Foods that you add to your list will appear here")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                foods.forEach {
                    item {
                        Food(
                            food = it,
                            setSelectedFoodToLog,
                            onSelectedFoodToLog
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun Food(
    food: FoodInformation,
    setSelectedFood: (FoodInformation) -> Unit,
    onSelectedFood: () -> Unit
) {
    val missingBrand = food.information.brand == null || food.information.brand == ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .areaContainerSmall(
                onClick = {
                    setSelectedFood(food)
                    onSelectedFood()
                }
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            Text(
                text = food.information.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (missingBrand) "~" else food.information.brand,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    food.nutrients.basic.forEach { (nutrientData, amount) ->
                        val nutrientColor = getNutrientColor(nutrientData.preferences.hexColor) ?: Color(0xFFFFFFFF)

                        Text(
                            text = doubleFormatter(amount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = nutrientColor
                        )
                    }
                }
            )
        }
    )
}
