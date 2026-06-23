package com.example.fitnessway.ui.nutrient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsBox
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsCircle

@Composable
fun PagedNutrients(
    nutrients: List<NutrientDataAmount>,
    apiNutrients: List<NutrientData>,
    viewFormat: NutrientsViewFormat,
    isDataMinimal: Boolean = false,
    isBasicNutrient: Boolean = true,
    isBaseSizeDisplay: Boolean = true,
    isUserPremium: Boolean,
    onViewNutrientAnalytics: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    val childMap = nutrients
        .filter { it.data.configuration.parentId != null }
        .groupBy { it.data.configuration.parentId }

    val parentList = childMap
        .mapNotNull { (parentId, children) ->
            if (parentId in nutrients
                    .map { it.data.base.id }
                    .toSet()
            ) {
                // Parent already exists in the food with its own value, skip
                null
            } else {
                // Parent not in food, create it with sum of children
                apiNutrients
                    .find { an -> an.base.id == parentId }
                    ?.let { found ->
                        NutrientDataAmount(
                            data = found,
                            amount = children.sumOf { it.amount }
                        )
                    }
            }
        }

    val chunkedNutrients = nutrients
        .filter { it.data.configuration.parentId == null }
        .plus(parentList)
        .sortedBy { it.bySortOrder }
        .chunked(4)

    var parentIdTapped by remember { mutableIntStateOf(0) }
    var areChildrenVisible by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { chunkedNutrients.size })

    Box(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Box content measure
        val contentWidth = if (isBaseSizeDisplay) 72.dp else 62.dp

        val progressBarHeight = if (isBaseSizeDisplay) {
            if (isBasicNutrient) 115.2.dp
            else (115.2 / 2).dp
        } else {
            if (isBasicNutrient) (contentWidth * 0.7f) * 2
            else contentWidth * 0.8f
        }

        val verticalSpace = if (isBaseSizeDisplay) 10.dp else 12.dp

        @Composable
        fun getNutrientsAsBox(
            nutrients: List<NutrientDataAmount>,
            onHasChildren: ((Int) -> Boolean)? = null,
            onToggleChildren: (Int) -> Unit,
        ) = NutrientsAsBox(
            nutrients = nutrients,
            isDataMinimal = isDataMinimal,
            isUserPremium = isUserPremium,
            progressBarHeight = progressBarHeight,
            contentWidth = contentWidth,
            verticalSpace = verticalSpace,
            onHasChildren = onHasChildren,
            onViewAnalytics = { onViewNutrientAnalytics?.invoke(it) },
            onToggleChildren = { onToggleChildren(it) }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = modifier.fillMaxWidth()
            ) { page ->

                fun onViewChildren(id: Int) {
                    if (childMap.containsKey(id)) {
                        parentIdTapped = id
                        areChildrenVisible = true
                    }
                }

                when (viewFormat) {
                    NutrientsViewFormat.BOX -> getNutrientsAsBox(
                        nutrients = chunkedNutrients[page],
                        onHasChildren = { childMap.containsKey(it) },
                        onToggleChildren = { onViewChildren(it) },
                    )

                    NutrientsViewFormat.CIRCLE -> NutrientsAsCircle(
                        nutrients = chunkedNutrients[page],
                        isUserPremium = isUserPremium,
                        onHasChildren = { childMap.containsKey(it) },
                        onToggleChildren = { onViewChildren(it) }
                    )
                }
            }

            Spacer(Modifier.padding(top = 16.dp))

            if (areChildrenVisible) {
                UNutrient.Ui.GetChildrenToggleButton(
                    isChild = true,
                    onToggle = { areChildrenVisible = false },
                )
            }

            if (chunkedNutrients.size > 1 && !areChildrenVisible) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(chunkedNutrients.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = areChildrenVisible,
            enter = Animation.ComposableTransition.fadeIn,
            exit = Animation.ComposableTransition.fadeOut
        ) {
            Box(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                val nutrientChildren = childMap[parentIdTapped] ?: emptyList()

                when (viewFormat) {
                    NutrientsViewFormat.BOX -> getNutrientsAsBox(
                        nutrients = nutrientChildren,
                        onToggleChildren = { areChildrenVisible = false }
                    )

                    NutrientsViewFormat.CIRCLE -> NutrientsAsCircle(
                        nutrients = nutrientChildren,
                        isUserPremium = isUserPremium,
                        onToggleChildren = { areChildrenVisible = false }
                    )
                }
            }
        }
    }
}
