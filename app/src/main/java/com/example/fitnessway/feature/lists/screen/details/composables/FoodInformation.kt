package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.util.Food.FoodComposables

@Composable
fun FoodInformation(
    food: FoodInformation,
    onEdit: () -> Unit,
    shouldOverlayAppear: Boolean,
) {
    Column(
        content = {
            val foodComposables = remember(food) {
                FoodComposables(food)
            }

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

                    item {
                        ToggleEditButton(
                            text = "Edit",
                            onClick = onEdit,
                            backgroundColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    )

    BlurOverlay(isVisible = shouldOverlayAppear)
}