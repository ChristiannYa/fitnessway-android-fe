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
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Nutrient
import com.example.fitnessway.util.Nutrient.Ui.PagedNutrients
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
                    .areaContainer(
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
                        PagedNutrients(
                            nutrients = nutrients,
                            displayFormat = Nutrient.ScrollableNutrientsFormat.BOX,
                            isBasicNutrient = false,
                            isUserPremium = user.isPremium,
                        )
                    }
                }
            )
        }

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}