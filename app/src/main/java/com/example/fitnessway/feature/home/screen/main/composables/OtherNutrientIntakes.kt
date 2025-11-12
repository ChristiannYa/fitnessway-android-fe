package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.calcNutrientIntakeData
import com.example.fitnessway.util.Nutrient.filterDisplayedNutrients
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    nutrientType: NutrientType,
    user: User,
) {
    val nutrientTypeName = nutrientType.name.replaceFirstChar { it.uppercase() }

    when (state) {
        is UiState.Loading -> Text("Loading basic $nutrientTypeName intakes")

        is UiState.Success -> OtherNutrients(
            nutrients = state.data,
            nutrientType = nutrientType,
            user = user
        )

        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
fun OtherNutrients(
    nutrients: NutrientIntakesByType,
    nutrientType: NutrientType,
    user: User
) {
    val nutrientTypeName = nutrientType.name.lowercase().replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.areaContainerLarge(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val displayedNutrients = filterDisplayedNutrients(
                nutrientsByType = nutrients,
                nutrientType = nutrientType,
                user = user
            )

            if (displayedNutrients.isEmpty()) {
                Text(
                    text = "Set your $nutrientTypeName goals in order to see their intake progress",
                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                displayedNutrients.forEach { nutrient ->
                    OtherNutrient(nutrient)
                }
            }
        }
    )
}

@Composable
fun OtherNutrient(
    intakeData: NutrientIntake,
    modifier: Modifier = Modifier
) {
    val data = remember(key1 = intakeData) { calcNutrientIntakeData(intakeData) }

    val itemRadius = 16.dp

    val intakeComposables = remember(intakeData) {
        IntakesComposables(intake = intakeData)
    }

    Column(
        modifier = Modifier
            .width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            intakeComposables.intakeGoal()

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
                        shape = RoundedCornerShape(itemRadius),
                    )
                    .clip(RoundedCornerShape(itemRadius)),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((data.progress / 100f).toFloat())
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                            )
                            .align(Alignment.BottomCenter),
                    )
                    intakeComposables.intakeProgress(
                        data = data,
                        modifier
                            .align(Alignment.Center)
                    )
                }
            )

            intakeComposables.intakeName()
        }
    )
}