package com.example.fitnessway.feature.home.screen.logdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Food.FoodComposables
import org.koin.androidx.compose.koinViewModel

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
                title = "Food Log Details",
                extraContent = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable(
                                onClick = {}
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                        content = {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                fontFamily = robotoSerifFamily
                            )
                        }
                    )
                }
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
                                        verticalSpace = verticalSpace
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
            }
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