package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.ApiErrorMessage
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.util.Nutrient.Ui.NutrientsBoxUi
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
            NutrientsBoxUi(
                nutrients = nutrients,
                nutrientType = NutrientType.BASIC,
                user = user,
            )
        }
    )
}