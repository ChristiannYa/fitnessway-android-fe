package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.util.UFood.FoodComposables

@Composable
fun FoodInformation(
    food: FoodInformation,
    shouldOverlayAppear: Boolean,
    onOverlayClick: () -> Unit,
    user: User
) {
    val foodComposables = remember(food) {
        FoodComposables(food = food, user = user)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        content = {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                content = {
                    item {
                        foodComposables.BaseInformation()
                    }

                    item {
                        foodComposables.NutrientSummary()
                    }

                    item {
                        foodComposables.RemainingNutrients()
                    }
                }
            )

            BlurOverlay(
                isVisible = shouldOverlayAppear,
                onClick = onOverlayClick
            )
        }
    )
}