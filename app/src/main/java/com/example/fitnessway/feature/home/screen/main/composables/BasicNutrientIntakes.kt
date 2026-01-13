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
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.ui.shared.Loading.Composable
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.UNutrient.Ui.PagedNutrients
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UiState

@Composable
fun BasicNutrientIntakes(
    state: UiState<NutrientsByType<NutrientDataWithAmount>>,
    user: User,
    onNavigateToGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Loading -> Composable(210.dp, "Loading nutrient intakes")

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
                        message = "Set your nutrient goals to see intake progress"
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nutrients",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        PagedNutrients(
                            nutrients = nutrients,
                            displayFormat = UNutrient.ScrollableNutrientsFormat.BOX,
                            isUserPremium = user.isPremium
                        )
                    }
                }
            }
        }

        else -> {}
    }
}