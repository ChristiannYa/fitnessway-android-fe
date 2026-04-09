package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UiState

@Composable
fun UserFoodsList(
    foodsUiState: UiState<List<MFood.Model.FoodInformation>>,
    isVisible: Boolean,
    isUserPremium: Boolean,
    onFoodClick: (MFood.Model.FoodInformation) -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UFood.Ui.UserFoodsList(
                state = foodsUiState,
                isUserPremium = isUserPremium,
                showsNutrientPreview = true,
                onFoodClick = onFoodClick
            )

            NotFoundMessageAnimated(
                isVisible = foodsUiState is UiState.Error,
                message = foodsUiState.toErrorMessageOrNull() ?: ""
            )
        }
    }
}