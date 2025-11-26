package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
import com.example.fitnessway.util.Food.FoodComposables
import com.example.fitnessway.util.Formatters.doubleFormatter
import org.koin.androidx.compose.koinViewModel

private class Field(
    val key: String,
    val value: String,
    val isEditable: Boolean? = false
)

@Composable
fun LogDetailsScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val selectedFoodLog by viewModel.selectedFoodLog.collectAsState()

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Food Log Details"
            )
        },
        content = {
            val foodLog = selectedFoodLog

            if (foodLog == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Food log information not found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                val foodComposables = remember(foodLog.food) {
                    FoodComposables(foodLog.food)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    content = {
                        stickyHeader {
                            Details(foodLog)
                        }

                        item {
                            foodComposables.NutrientSummary()
                        }

                        item {
                            foodComposables.RemainingNutrients()
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun Details(foodLog: FoodLogData) {
    val aps = foodLog.servings * foodLog.food.information.amountPerServing

    val fields = listOf(
        Field(
            key = "Category",
            value = foodLog.category.replaceFirstChar { it.uppercase() }
        ),
        Field(
            key = "Time",
            value = foodLog.time
        ),
        Field(
            key = "Amount Per Serving (${foodLog.food.information.servingUnit})",
            value = doubleFormatter(aps),
            isEditable = true
        ),
        Field(
            key = "Servings",
            value = doubleFormatter(foodLog.servings),
            isEditable = true
        )
    )

    Column(
        modifier = Modifier.areaContainerMedium(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            Text(
                text = foodLog.food.information.name,
                style = MaterialTheme.typography.titleSmall
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = {
                    fields.forEachIndexed { index, field ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                Text(
                                    text = field.key,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = field.value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        )

                        if (index < fields.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.1f)
                            )
                        }
                    }
                }
            )
        }
    )
}