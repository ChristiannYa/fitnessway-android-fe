package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
fun BasicNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    user: User
) {
    when (state) {
        is UiState.Loading -> Text("Loading basic nutrient intakes")
        is UiState.Success -> BasicNutrients(state.data, user)
        is UiState.Error -> ApiErrorMessage(state.message)
        is UiState.Idle -> {}
    }
}

@Composable
fun BasicNutrients(nutrients: NutrientIntakesByType, user: User) {
    Row(
        modifier = Modifier.areaContainerLarge(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val displayedNutrients = filterDisplayedNutrients(
                nutrientsByType = nutrients,
                nutrientType = NutrientType.BASIC,
                user = user
            )

            if (displayedNutrients.isEmpty()) {
                Text(
                    text = "Set your Nutrient goals in order to see their intake progress",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                displayedNutrients.forEach { nutrient ->
                    BasicNutrient(nutrient)
                }
            }
        }
    )
}

@Composable
fun BasicNutrient(
    intakeData: NutrientIntake,
    modifier: Modifier = Modifier
) {
    val data = remember(intakeData) { calcNutrientIntakeData(intakeData) }

    val barRadius = 16.dp
    val spacedBy = 12.dp

    val intakeComposables = remember(intakeData) {
        IntakesComposables(intake = intakeData)
    }

    Column(
        modifier = modifier
            .width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacedBy),
        content = {
            intakeComposables.intakeGoal()

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(150.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
                        shape = RoundedCornerShape(barRadius),
                    )
                    .clip(RoundedCornerShape(barRadius)),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((data.progress / 100f).toFloat())
                            .background(
                                color = intakeComposables.nutrientColor,
                                shape = RoundedCornerShape(
                                    bottomStart = barRadius,
                                    bottomEnd = barRadius,
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )

                    intakeComposables.intakeProgress(
                        data = data,
                        modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(spacedBy * 2))
                    )
                }
            )

            intakeComposables.intakeName(data = data)
        }
    )
}