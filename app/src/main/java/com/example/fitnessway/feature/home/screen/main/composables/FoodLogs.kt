package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.mappers.toPascalCaseSpaced
import com.example.fitnessway.data.model.MFood.Enum.FoodLogFoodStatus
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.m_26.FoodLogCategories
import com.example.fitnessway.ui.shared.Loading.Area
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.OrangeWarning
import com.example.fitnessway.ui.theme.WhiteBackground
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.UFood.Ui.getFoodBrandColor
import com.example.fitnessway.util.UFood.Ui.getFoodBrandText
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.UiState

@Composable
private fun getFoodLogsContainerColor(): Color {
    return MaterialTheme.colorScheme.surfaceTint
}

@Composable
fun FoodLogs(
    foodLogsState: UiState<FoodLogsByCategory>,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit,
    isDeletionError: Boolean,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.VerticalSlideFromTop.enter,
        exit = Animation.ComposableTransition.VerticalSlideFromTop.exit,
        modifier = modifier
            .height(Ui.Measurements.UPWARDS_SLIDEABLE_HEIGHT_SMALL)
            .zIndex(2f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .areaContainer(
                    areaColor = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .fillMaxHeight()
        ) {
            when (foodLogsState) {
                is UiState.Loading -> Area(text = "Loading logs")

                is UiState.Success -> FoodLogsCategorized(
                    foodLogs = foodLogsState.data,
                    onViewFoodsList = onViewFoodsList,
                    onViewFoodLogDetails = onViewFoodLogDetails,
                    onRemoveFoodLog = onRemoveFoodLog,
                    isDeletionError = isDeletionError
                )

                else -> {}
            }

            NotFoundMessageAnimated(
                isVisible = foodLogsState is UiState.Error,
                message = foodLogsState.toErrorMessageOrNull() ?: ""
            )
        }
    }
}


@Composable
private fun FoodLogsCategorized(
    foodLogs: FoodLogsByCategory,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit,
    isDeletionError: Boolean
) {
    val categoriesWithLogs = listOf(
        FoodLogCategories.BREAKFAST to foodLogs.breakfast,
        FoodLogCategories.LUNCH to foodLogs.lunch,
        FoodLogCategories.DINNER to foodLogs.dinner,
        FoodLogCategories.SUPPLEMENT to foodLogs.supplement
    )

    LazyColumn {
        categoriesWithLogs.forEachIndexed { index, (category, logs) ->
            stickyHeader {
                Text(
                    text = category.name.toPascalCaseSpaced(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(
                            top = if (index > 0) AreaContainerSize.LARGE.padding else 0.dp,
                            bottom = AreaContainerSize.LARGE.padding
                        )
                )
            }

            item {
                FoodLogCategory(
                    foodLogs = logs,
                    category = category,
                    onViewFoodsList = onViewFoodsList,
                    onViewFoodLogDetails = onViewFoodLogDetails,
                    onRemoveFoodLog = onRemoveFoodLog,
                    isDeletionError = isDeletionError
                )
            }
        }
    }
}

@Composable
private fun FoodLogCategory(
    foodLogs: List<FoodLogData>,
    category: FoodLogCategories,
    onViewFoodsList: (FoodLogCategories) -> Unit,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit,
    isDeletionError: Boolean
) {
    val categoryFormatted = category.name.toPascalCaseSpaced()

    Column(
        modifier = Modifier
            .areaContainer(
                size = AreaContainerSize.MEDIUM,
                shape = RoundedCornerShape(28.dp),
                areaColor = getFoodLogsContainerColor()
            )
            .graphicsLayer(clip = true),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (foodLogs.isEmpty()) {
            NotFoundMessage(message = "No $categoryFormatted logged yet")
        } else {
            foodLogs.forEach { log ->
                key(log.id) {
                    FoodLog(
                        foodLog = log,
                        onViewFoodLogDetails = { onViewFoodLogDetails(log) },
                        onRemoveFoodLog = { onRemoveFoodLog(log) },
                        isDeletionError = isDeletionError
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
            )
        ) {
            Text(
                text = "Log",
                fontFamily = robotoSerifFamily,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodLog(
    foodLog: FoodLogData,
    onViewFoodLogDetails: (FoodLogData) -> Unit,
    onRemoveFoodLog: (FoodLogData) -> Unit,
    isDeletionError: Boolean
) {
    val swipeToRemoveLogState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled
    )

    LaunchedEffect(swipeToRemoveLogState.settledValue) {
        if (swipeToRemoveLogState.settledValue == SwipeToDismissBoxValue.EndToStart) {
            onRemoveFoodLog(foodLog)
        }
    }

    LaunchedEffect(isDeletionError) {
        if (isDeletionError) {
            swipeToRemoveLogState.reset()
        }
    }

    SwipeToDismissBox(
        state = swipeToRemoveLogState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            when (swipeToRemoveLogState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {}
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = getFoodLogsContainerColor(),
                )
                .padding(
                    end = 2.dp,
                    start = 2.dp
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onViewFoodLogDetails(foodLog) },
                )
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val foodBrandColor = getFoodBrandColor()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = foodLog.food.information.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, false)
                    )

                    if (foodLog.foodStatus == FoodLogFoodStatus.PRESENT &&
                        foodLog.food.metadata.isFavorite
                    ) {
                        Structure.AppIconDynamic(
                            source = Structure.AppIconButtonSource.Vector(
                                imageVector = Icons.Default.Star
                            ),
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    if (foodLog.foodStatus != FoodLogFoodStatus.PRESENT) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(
                                    color = when (foodLog.foodStatus) {
                                        FoodLogFoodStatus.DELETED -> OrangeWarning
                                        FoodLogFoodStatus.UPDATED -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Text(
                    text = getFoodBrandText(foodLog.food.information.brand),
                    style = MaterialTheme.typography.labelLarge,
                    color = foodBrandColor
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = FontFamily.Default
                            ),
                            block = {
                                append(
                                    text = doubleFormatter(foodLog.servings, 3)
                                )
                            }
                        )
                        append(
                            text = run {
                                " " + if (foodLog.servings == 1.0) "serving" else "servings"
                            }
                        )
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = foodBrandColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            AppLabel<Unit>(
                text = foodLog.time,
                textStyle = MaterialTheme.typography.labelSmall,
                size = Ui.LabelSize.XS,
            )
        }
    }
}