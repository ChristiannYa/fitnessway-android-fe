package com.example.fitnessway.feature.home.screen.main.composables

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.Ui.NutrientsAsBox
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.UiState

@Composable
fun BasicNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    user: User,
    onNavigateToGoals: () -> Unit
) {
    // @TODO: Figure out why when this composable enters the screen it recomposes

    when (state) {
        is UiState.Loading -> Text("Loading basic nutrient intakes")

        is UiState.Success -> {
            val nutrients = filterNutrientsByType(
                nutrients = state.data,
                type = NutrientType.BASIC
            ).filter {
                it.nutrientWithPreferences.preferences.goal != null &&
                        (!it.nutrientWithPreferences.nutrient.isPremium || user.isPremium)
            }

            val isEmpty = nutrients.isEmpty()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .areaContainerLarge(
                        showsIndication = true,
                        onClickEnabled = isEmpty,
                        onClick = onNavigateToGoals
                    ),
                content = {
                    if (isEmpty) {
                        NotFoundText(
                            text = "Set your nutrient goals to see intake progress"
                        )
                    } else {
                        val chunkedNutrients = nutrients.chunked(4)
                        val pagerState = rememberPagerState(pageCount = { chunkedNutrients.size })

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    NutrientsAsBox(chunkedNutrients[page], user)
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
                }
            )
        }

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}