package com.example.fitnessway.feature.lists.screen.user_details.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.model.m_26.FoodInformation
import com.example.fitnessway.ui.food.FoodInformationView
import com.example.fitnessway.util.Animation

// @TODO: Change `user` parameter to `isUserPremium`
@Composable
fun FoodInformation(
    food: FoodInformation,
    isFoodDeletionSuccess: Boolean,
    user: User,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isFoodDeletionSuccess,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            FoodInformationView(food, user.isPremium)
        }
    }
}