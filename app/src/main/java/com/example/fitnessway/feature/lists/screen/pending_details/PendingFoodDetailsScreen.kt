package com.example.fitnessway.feature.lists.screen.pending_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toFoodInformation
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.PendingFoodStatus
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.edible.FoodInformationView
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.consumeTap
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.PopupOrigin
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.extensions.getAccent
import org.koin.androidx.compose.koinViewModel

@Composable
fun PendingFoodDetailsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val userRepoUiState by viewModel.userRepoUiState.collectAsState()
    val nutrientRepoUiState by viewModel.nutrientRepoUiState.collectAsState()

    val user = userRepoUiState.userUiState.toSuccessOrNull()
    val nutrientsUiState = nutrientRepoUiState.nutrients
    val pendingFood = viewModel.requestManager.pendingEdible.collectAsState().value

    val isScreenDataReady = user != null && pendingFood != null

    var isRejectionReasonVisible by remember { mutableStateOf(false) }

    if (isScreenDataReady) {

        Screen(
            header = {
                Header(
                    onBackClick = onBackClick,
                    title = "My Food Request"
                ) {
                    Ui.AppLabel(
                        text = pendingFood.status.name.toTitleCase(),
                        textColor = pendingFood.status.getAccent(),
                        size = Ui.LabelSize.MEDIUM,
                        data = Unit,
                        clickableConfiguration = if (pendingFood.status == PendingFoodStatus.REJECTED) {
                            Ui.ClickableConfiguration(
                                onClick = { isRejectionReasonVisible = !isRejectionReasonVisible },
                            )
                        } else null
                    )
                }
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                FoodInformationView(
                    food = pendingFood.toFoodInformation(),
                    apiNutrients = nutrientsUiState.toSuccessOrNull()?.toList() ?: emptyList(),
                    isUserPremium = user.isPremium
                )

                DarkOverlay(
                    isVisible = isRejectionReasonVisible,
                    onClick = { isRejectionReasonVisible = false }
                )

                AnimatedVisibility(
                    visible = isRejectionReasonVisible,
                    enter = Animation.ComposableTransition.ScaleWithSpring.enter(PopupOrigin.CENTER),
                    exit = Animation.ComposableTransition.ScaleWithSpring.exit(PopupOrigin.CENTER),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(Ui.Measurements.POP_UP_MESSAGE_WIDTH)
                            .areaContainer(
                                areaColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            .consumeTap()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Rejection Reason",
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${pendingFood.rejectionReason}",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}