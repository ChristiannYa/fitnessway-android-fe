package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.shared.TextWithLoadingIndicator
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.util.Food
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.getColor
import com.example.fitnessway.util.UiState

@Composable
fun Foods(
    state: UiState<List<FoodInformation>>,
    setSelectedFoodToLog: (FoodInformation) -> Unit,
    onSelectedFoodToLog: () -> Unit,
    user: User
) {
    when (state) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TextWithLoadingIndicator(
                    loadingText = "Loading Foods"
                )
            }
        }
        is UiState.Success -> {
            Foods(state.data, setSelectedFoodToLog, onSelectedFoodToLog, user)
        }
        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
private fun Foods(
    foods: List<FoodInformation>,
    setSelectedFoodToLog: (FoodInformation) -> Unit,
    onSelectedFoodToLog: () -> Unit,
    user: User
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
                            setSelectedFood = setSelectedFoodToLog,
                            onSelectedFood = onSelectedFoodToLog,
                            user = user
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun Food(
    food: FoodInformation,
    setSelectedFood: (FoodInformation) -> Unit,
    onSelectedFood: () -> Unit,
    user: User
) {
    val foodBrandText = Food.Ui.getFoodBrandText(food.information.brand)
    val foodBrandColor = Food.Ui.getFoodBrandColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .areaContainer(
                size = AreaContainerSize.SMALL,
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = foodBrandText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = foodBrandColor
            )

            if (user.isPremium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        food.nutrients.basic.forEach { (nutrientData, amount) ->
                            val nutrientColor = getColor(nutrientData.preferences.hexColor)

                            if (nutrientColor != null) {
                                Text(
                                    text = doubleFormatter(amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = nutrientColor
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}
