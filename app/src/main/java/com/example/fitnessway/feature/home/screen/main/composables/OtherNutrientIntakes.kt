package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.Ui.NutrientsBoxUi
import com.example.fitnessway.util.UiState


@Composable
fun OtherNutrientIntakes(
    state: UiState<NutrientIntakesByType>,
    nutrientType: NutrientType,
    user: User,
) {
    val nutrientTypeName = nutrientType.name.replaceFirstChar { it.uppercase() }

    when (state) {
        is UiState.Loading -> Text("Loading $nutrientTypeName intakes")

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
    Row(
        modifier = Modifier.areaContainerLarge(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            NutrientsBoxUi(
                nutrients = nutrients,
                nutrientType = nutrientType,
                user = user,
                progressBarHeight = 60.dp
            )
        }
    )
}