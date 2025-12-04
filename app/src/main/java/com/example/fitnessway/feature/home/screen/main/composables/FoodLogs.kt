package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogFoodStatus
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium
import com.example.fitnessway.ui.theme.OrangeWarning
import com.example.fitnessway.ui.theme.WhiteBackground
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UiState

@Composable
fun FoodLogs(
    foodLogsState: UiState<FoodLogsByCategory>,
    foodLogDeleteState: UiState<FoodLogData>,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit
) {
    when (foodLogsState) {
        is UiState.Loading -> Text("Loading food logs")

        is UiState.Success -> FoodLogsCategorized(
            foodLogs = foodLogsState.data,
            foodLogDeleteState = foodLogDeleteState,
            onViewFoodsList = onViewFoodsList,
            onViewFoodLogDetails = onViewFoodLogDetails,
            onRemoveFoodLog = onRemoveFoodLog
        )

        is UiState.Error -> ApiErrorMessage(foodLogsState.message)
        is UiState.Idle -> {}
    }
}


@Composable
fun FoodLogsCategorized(
    foodLogs: FoodLogsByCategory,
    foodLogDeleteState: UiState<FoodLogData>,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit
) {
    val categoriesWithLogs = listOf(
        FoodLogCategories.BREAKFAST to foodLogs.breakfast,
        FoodLogCategories.LUNCH to foodLogs.lunch,
        FoodLogCategories.DINNER to foodLogs.dinner,
        FoodLogCategories.SUPPLEMENT to foodLogs.supplement
    )

    Column(
        modifier = Modifier.areaContainerLarge(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = {
            categoriesWithLogs.forEach { (category, logs) ->
                FoodLogCategory(
                    foodLogs = logs,
                    category = category,
                    foodLogDeleteState = foodLogDeleteState,
                    onViewFoodsList = onViewFoodsList,
                    onViewFoodLogDetails = onViewFoodLogDetails,
                    onRemoveFoodLog = onRemoveFoodLog,
                )
            }
        }
    )
}

@Composable
fun FoodLogCategory(
    foodLogs: List<FoodLogData>,
    foodLogDeleteState: UiState<FoodLogData>,
    category: FoodLogCategories,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit
) {
    val categoryFormatted = category.name.lowercase().replaceFirstChar { it.uppercase() }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = {
            Text(
                text = categoryFormatted,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .areaContainerMedium(
                        areaColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    .graphicsLayer(clip = true),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                content = {
                    if (foodLogs.isEmpty()) {
                        Text(
                            text = "No $categoryFormatted logged yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        foodLogs.forEach { log ->
                            key(log.id) {
                                FoodLog(
                                    foodLog = log,
                                    foodLogDeleteState = foodLogDeleteState,
                                    onViewFoodLogDetails = { onViewFoodLogDetails(log) },
                                    onRemoveFoodLog = { onRemoveFoodLog(log) },
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { onViewFoodsList(category) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLog(
    foodLog: FoodLogData,
    foodLogDeleteState: UiState<FoodLogData>,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit
) {
    val food = foodLog.food.information

    val hasCalled = remember { mutableStateOf(false) }

    val swipeToRemoveLogState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == EndToStart && !hasCalled.value) {
                hasCalled.value = true
                onRemoveFoodLog(foodLog)
            }
            it == EndToStart
        }
    )

    LaunchedEffect(foodLogDeleteState) {
        when (foodLogDeleteState) {
            is UiState.Error -> {
                swipeToRemoveLogState.reset()
                hasCalled.value = false
            }

            is UiState.Idle -> {
                hasCalled.value = false
            }

            else -> {}
        }
    }

    SwipeToDismissBox(
        state = swipeToRemoveLogState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            when (swipeToRemoveLogState.dismissDirection) {
                EndToStart -> {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = lerp(
                                    start = MaterialTheme.colorScheme.inverseSurface,
                                    stop = MaterialTheme.colorScheme.errorContainer,
                                    fraction = swipeToRemoveLogState.progress
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }

                else -> {}
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    )
                    .padding(
                        end = 2.dp,
                        start = 2.dp
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onViewFoodLogDetails(foodLog) },
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                content = {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    Text(
                                        text = food.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    val dotInfoColor = when (foodLog.foodStatus) {
                                        FoodLogFoodStatus.DELETED -> OrangeWarning
                                        FoodLogFoodStatus.UPDATED -> MaterialTheme.colorScheme.secondary
                                        else -> Color.Transparent
                                    }

                                    if (foodLog.foodStatus != FoodLogFoodStatus.PRESENT) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .background(
                                                    color = dotInfoColor,
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
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = FontFamily.Default
                                        ),
                                        block = {
                                            append(
                                                text = doubleFormatter(foodLog.servings)
                                            )
                                        }
                                    )
                                    append(
                                        text = " Servings"
                                    )
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = foodLog.time,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        modifier = Modifier.wrapContentWidth(unbounded = false)
                    )
                }
            )
        }
    )
}