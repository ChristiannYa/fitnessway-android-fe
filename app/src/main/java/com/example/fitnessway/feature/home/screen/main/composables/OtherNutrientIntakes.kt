package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.fitnessway.util.Nutrient.Ui.NutrientsBoxUi
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    nutrientType: NutrientType,
    user: User,
    onNavigateToGoals: () -> Unit,
) {
    val nutrientTypeName = nutrientType.name.lowercase().replaceFirstChar { it.uppercase() }

    when (state) {
        is UiState.Loading -> Text("Loading $nutrientTypeName intakes")

        is UiState.Success -> {
            val nutrients = filterNutrientsByType(
                nutrients = state.data,
                type = nutrientType
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
                            text = "Set your $nutrientTypeName goals to see their progress"
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                NutrientsBoxUi(
                                    nutrients = nutrients,
                                    nutrientType = nutrientType,
                                    progressBarHeight = (115.2 / 2).dp
                                )
                            }
                        )
                    }
                }
            )
        }

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}