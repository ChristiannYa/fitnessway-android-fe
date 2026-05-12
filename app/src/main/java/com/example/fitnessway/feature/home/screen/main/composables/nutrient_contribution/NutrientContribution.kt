package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toListByCategory
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.consumeTap
import com.example.fitnessway.util.Animation.ComposableTransition
import com.example.fitnessway.util.PopupOrigin
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.Ui
import kotlin.time.Instant

@Composable
fun NutrientContribution(
    edibleLogs: FoodLogsCategorized?,
    nutrientList: List<MNutrient.Model.NutrientWithPreferences>?,
    nutrientId: Int,
    isVisible: Boolean,
    isUserPremium: Boolean,
    formatTime: (Instant) -> String,
    modifier: Modifier = Modifier
) {
    edibleLogs?.let { logsCategorized ->
        AnimatedVisibility(
            visible = isVisible,
            enter = ComposableTransition.ScaleWithSpring.enter(PopupOrigin.CENTER),
            exit = ComposableTransition.ScaleWithSpring.exit(PopupOrigin.CENTER),
            modifier = modifier
                .then(
                    if (isVisible) {
                        Modifier.consumeTap()
                    } else Modifier
                )
        ) {
            val logListByCategory = logsCategorized.toListByCategory()

            nutrientList
                ?.find { it.nutrient.id == nutrientId }
                ?.let { nutrientData ->

                    val nutrientColor = UNutrient.getUserNutrientColor(
                        nutrientData.preferences.hexColor, isUserPremium
                    )

                    Box(
                        modifier = Modifier
                            .width(Ui.Measurements.POP_UP_CONTAINER_WIDTH)
                            .heightIn(0.dp, Ui.Measurements.POP_UP_HEIGHT_MEDIUM)
                            .areaContainer(
                                areaColor = MaterialTheme.colorScheme.surfaceVariant,
                                borderColor = nutrientColor.copy(0.8f),
                                borderWidth = 1.dp,
                                isBottomPaddingIncluded = false
                            )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                if (logListByCategory.all { (_, logs) -> logs.isEmpty() }) {
                                    0.dp
                                } else 12.dp
                            )
                        ) {
                            NutrientContributionHeader(
                                logs = logListByCategory.flatMap { (_, logs) -> logs },
                                nutrientData = nutrientData,
                                nutrientColor = nutrientColor
                            )

                            val listState = rememberLazyListState()

                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                logListByCategory
                                    .filter { (_, logs) ->
                                        logs.any { log ->
                                            log.edibleInformation.nutrients
                                                .toList()
                                                .any { it.data.base.id == nutrientId }
                                        }
                                    }
                                    .let { availableLogListsByCategory ->
                                        availableLogListsByCategory.forEachIndexed { index, (category, availableLogs) ->

                                            val isLast = index == availableLogListsByCategory.lastIndex

                                            stickyHeader(key = "nutrient_contribution_header_${category.name}") {
                                                Text(
                                                    text = category.name.toTitleCase(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                        .padding(bottom = 2.dp)
                                                )
                                            }

                                            item(key = "nutrient_contribution_items_${category.name}") {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier
                                                        .padding(
                                                            bottom = if (isLast) 0.dp else 10.dp
                                                        )
                                                ) {
                                                    NutrientContributionOverview(
                                                        logs = availableLogs,
                                                        nutrientData = nutrientData,
                                                        nutrientColor = nutrientColor,
                                                        formatTime = formatTime
                                                    )
                                                }
                                            }

                                            if (isLast) {
                                                item(key = "nutrient_contribution_bottom_spacer") {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                }
                                            }
                                        }
                                    }

                                if (logListByCategory.all { (_, logs) -> logs.isEmpty() }) {
                                    item(key = "nutrient_contribution_bottom_spacer") {
                                        Spacer(modifier = Modifier.height(AppModifiers.AreaContainerSize.LARGE.padding))
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}