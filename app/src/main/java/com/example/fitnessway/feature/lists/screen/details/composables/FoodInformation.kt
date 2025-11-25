package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.ui.shared.BlurOverlay
import com.example.fitnessway.util.Food.Composables.BaseInformation
import com.example.fitnessway.util.Food.Composables.NutrientSummary
import com.example.fitnessway.util.Food.Composables.RemainingNutrients

@Composable
fun FoodInformation(
    food: FoodInformation,
    onEdit: () -> Unit,
    shouldOverlayAppear: Boolean,
) {
    Column(
        content = {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                content = {
                    item {
                        BaseInformation(food)
                    }

                    item {
                        NutrientSummary(food.nutrients)
                    }

                    item {
                        RemainingNutrients(food.nutrients)
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