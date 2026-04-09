package com.example.fitnessway.ui.nutrient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsBox
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsCircle

@Composable
fun PagedNutrients(
    nutrients: List<NutrientDataAmount>,
    viewFormat: NutrientsViewFormat,
    isDataMinimal: Boolean = false,
    isBasicNutrient: Boolean = true,
    isBaseSizeDisplay: Boolean = true,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val chunkedNutrients = nutrients.chunked(4)
    val pagerState = rememberPagerState(pageCount = { chunkedNutrients.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxWidth()
        ) { page ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (viewFormat) {
                    NutrientsViewFormat.BOX -> {
                        val contentWidth = if (isBaseSizeDisplay) 72.dp else 62.dp

                        val progressBarHeight = if (isBaseSizeDisplay) {
                            if (isBasicNutrient) {
                                115.2.dp
                            } else (115.2 / 2).dp
                        } else {
                            if (isBasicNutrient) {
                                (contentWidth * 0.7f) * 2
                            } else {
                                contentWidth * 0.8f
                            }
                        }

                        val verticalSpace = if (isBaseSizeDisplay) 10.dp else 12.dp


                        NutrientsAsBox(
                            nutrients = chunkedNutrients[page],
                            isDataMinimal = isDataMinimal,
                            isUserPremium = isUserPremium,
                            progressBarHeight = progressBarHeight,
                            contentWidth = contentWidth,
                            verticalSpace = verticalSpace
                        )
                    }

                    NutrientsViewFormat.CIRCLE -> NutrientsAsCircle(
                        nutrients = chunkedNutrients[page],
                        isUserPremium = isUserPremium
                    )
                }
            }
        }

        if (chunkedNutrients.size > 1) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
}