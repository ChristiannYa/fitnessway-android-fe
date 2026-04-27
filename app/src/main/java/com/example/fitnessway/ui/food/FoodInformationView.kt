package com.example.fitnessway.ui.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.EdibleInformation
import com.example.fitnessway.util.UFood

@Composable
fun FoodInformationView(
    food: EdibleInformation,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val foodComposables = remember(food) {
        UFood.FoodInformationComposables(food, isUserPremium)
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
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
}