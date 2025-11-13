package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
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
            val foodLog = selectedFoodLog
            val headerTitle = foodLog?.food?.information?.name ?: "Food not found"

            Header(
                onBackClick = onBackClick,
                title = headerTitle
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.areaContainerMedium(),
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
        }
    )
}