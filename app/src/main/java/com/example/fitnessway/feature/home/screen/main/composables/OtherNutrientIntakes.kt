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
import com.example.fitnessway.util.UNutrient.filterGoalSetAmounts
import com.example.fitnessway.util.UNutrient.filterNonPremiumAmounts
import com.example.fitnessway.util.UNutrient.filterNutrientsByType
import com.example.fitnessway.util.UNutrient.getNutrientCategoryTitle
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientsByType<NutrientDataWithAmount>>,
    nutrientType: NutrientType,
    user: User,
    onNavigateToGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientTypeName = nutrientType.name.lowercase()

    when (state) {
        is UiState.Loading -> Composable(160.dp, "Loading $nutrientTypeName intakes")

        is UiState.Success -> {
            val nutrients = state.data
                .filterNutrientsByType(nutrientType)
                .filterNonPremiumAmounts(user.isPremium)
                .filterGoalSetAmounts()

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
                        val nutrientCategory = getNutrientCategoryTitle(nutrientType)

                        Text(
                            text = nutrientCategory,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        PagedNutrients(
                            nutrients = nutrients,
                            displayFormat = UNutrient.ScrollableNutrientsFormat.BOX,
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