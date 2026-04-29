package com.example.fitnessway.feature.lists.screen.pending_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toFoodInformation
import com.example.fitnessway.data.mappers.toPascalSpaced
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.edible.FoodInformationView
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.extensions.getAccent
import org.koin.androidx.compose.koinViewModel

@Composable
fun PendingFoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val user = viewModel.userFlow.collectAsState().value
    val pendingFood = viewModel.requestManager.pendingFood.collectAsState().value

    val isScreenDataReady = user != null && pendingFood != null

    if (isScreenDataReady) {
        Screen(
            header = {
                Header(
                    onBackClick = onBackClick,
                    title = "My Food Request"
                ) {
                    Ui.AppLabel<Unit>(
                        text = pendingFood.status.name.toPascalSpaced(),
                        textColor = pendingFood.status.getAccent(),
                        size = Ui.LabelSize.MEDIUM
                    )
                }
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedVisibility(
                        visible = true,
                        enter = Animation.ComposableTransition.fadeIn,
                        exit = Animation.ComposableTransition.fadeOut,
                    ) {
                        FoodInformationView(pendingFood.toFoodInformation(), user.isPremium)
                    }
                }
            }
        }
    }
}