package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Nutrient
import com.example.fitnessway.util.Nutrient.Ui.PagedNutrients
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
                    .areaContainer(
                        size = AreaContainerSize.LARGE,
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
                        PagedNutrients(
                            nutrients = nutrients,
                            displayFormat = Nutrient.ScrollableNutrientsFormat.BOX,
                            isUserPremium = user.isPremium
                        )
                    }
                }
            )
        }

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}