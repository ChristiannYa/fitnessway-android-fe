package com.example.fitnessway.feature.lists.screen.user_details.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.m_26.EdibleInformation
import com.example.fitnessway.ui.edible.FoodInformationView
import com.example.fitnessway.util.Animation

@Composable
fun FoodInformation(
    food: EdibleInformation,
    isFoodDeletionSuccess: Boolean,
    isUserPremium: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isFoodDeletionSuccess,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            FoodInformationView(food, isUserPremium)
        }
    }
}