package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.AppEdibleData
import com.example.fitnessway.data.model.m_26.EdibleScope
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UiState

@Composable
fun FoundEdibleByBarcode(
    isVisible: Boolean,
    isUserPremium: Boolean,
    scannedBarcode: String,
    edibleDataState: UiState<AppEdibleData?>,
    onDismiss: () -> Unit,
    onLog: (FoodToLogSearchCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.ScaleWithSpring.enter(),
        exit = Animation.ComposableTransition.ScaleWithSpring.exit(),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .areaContainer(
                    borderColor = MaterialTheme.colorScheme.primary
                )
        ) {
            when (edibleDataState) {
                is UiState.Success -> {
                    val edible = edibleDataState.data?.edible

                    if (edible != null) {
                        val edibleComposables = remember(edible.id) {
                            UFood.FoodInformationComposables(edible.information, isUserPremium)
                        }

                        val nutrientsScrollState = rememberScrollState()

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            edibleComposables.BaseInformation()

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(nutrientsScrollState)
                            ) {
                                edibleComposables.NutrientSummary()
                                edibleComposables.RemainingNutrients()
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        onLog(
                                            FoodToLogSearchCriteria(
                                                scope = EdibleScope.Barcode(scannedBarcode),
                                                source = EdibleSource.APP,
                                                edibleType = edible.information.type
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Log",
                                        fontFamily = robotoSerifFamily
                                    )
                                }

                                Button(
                                    onClick = onDismiss,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Ok",
                                        fontFamily = robotoSerifFamily
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Not found")
                            Text(
                                text = scannedBarcode,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.alpha(0.6f)
                            )
                        }
                    }
                }

                is UiState.Loading -> Loading.SpinnerInScreen()

                is UiState.Error -> Text("Error")

                else -> {}
            }
        }
    }
}