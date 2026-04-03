package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientIntakes
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.nutrient.NutrientsViewFormat
import com.example.fitnessway.ui.nutrient.PagedNutrients
import com.example.fitnessway.ui.shared.Loading.Composable
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.toReadable
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientIntakes>,
    nutrientType: NutrientType,
    isUserPremium: Boolean,
    onNavigateToGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientTypeName = nutrientType.name.lowercase()

    when (state) {
        is UiState.Loading -> Composable(160.dp, "Loading $nutrientTypeName intakes")

        is UiState.Success -> {
            val nutrients = when (nutrientType) {
                NutrientType.BASIC -> state.data.basic // Just to satisfy the linter
                NutrientType.VITAMIN -> state.data.vitamins
                NutrientType.MINERAL -> state.data.minerals
            }
                .let { if (!isUserPremium) it.filter { n -> !n.nutrientData.base.isPremium } else it }
                .filter { it.nutrientData.preferences.goal != null }

            val isEmpty = nutrients.isEmpty()

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .areaContainer(
                        size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                        borderColor = MaterialTheme.colorScheme.surfaceVariant,
                        showsIndication = true,
                        onClickEnabled = isEmpty,
                        onClick = onNavigateToGoals
                    )
            ) {
                if (isEmpty) {
                    NotFoundMessage(
                        message = "Set your $nutrientTypeName goals to see their progress"
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = nutrientType.toReadable(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        PagedNutrients(
                            nutrients = nutrients,
                            viewFormat = NutrientsViewFormat.BOX,
                            isBasicNutrient = false,
                            isUserPremium = isUserPremium,
                        )
                    }
                }
            }
        }

        is UiState.Error -> {}
        is UiState.Idle -> {}
    }
}