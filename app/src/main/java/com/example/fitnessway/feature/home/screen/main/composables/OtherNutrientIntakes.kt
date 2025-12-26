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
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.Loading.LoadingComposable
import com.example.fitnessway.ui.shared.NotFoundText
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Nutrient
import com.example.fitnessway.util.Nutrient.Ui.PagedNutrients
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.getNutrientCategoryTitle
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    nutrientType: NutrientType,
    user: User,
    onNavigateToGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientTypeName = nutrientType.name.lowercase().replaceFirstChar { it.uppercase() }

    when (state) {
        is UiState.Loading -> LoadingComposable(140.dp, "${nutrientTypeName}s")

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
                    NotFoundText(
                        text = "Set your $nutrientTypeName goals to see their progress"
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val nutrientCategory = getNutrientCategoryTitle(nutrientType)

                        Text(
                            text = nutrientCategory,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        PagedNutrients(
                            nutrients = nutrients,
                            displayFormat = Nutrient.ScrollableNutrientsFormat.BOX,
                            isBasicNutrient = false,
                            isUserPremium = user.isPremium,
                        )
                    }
                }
            }
        }

        is UiState.Error -> {}
        is UiState.Idle -> {}
    }
}