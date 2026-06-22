package com.example.fitnessway.feature.home.screen.foodselection.foodlog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toEdibleType
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.AppEdible
import com.example.fitnessway.data.model.m_26.AppEdibleData
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.consumeTap
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.UFood
import com.example.fitnessway.util.UiState

@Composable
fun FoundEdibleByBarcode(
    isVisible: Boolean,
    isUserPremium: Boolean,
    scannedBarcode: String,
    currentLogCategory: FoodLogCategory,
    edibleDataState: UiState<AppEdibleData?>,
    onDismiss: () -> Unit,
    onLog: (AppEdible, FoodLogCategory?) -> Unit,
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
            modifier = Modifier.animateContentSize()
        ) {
            when (edibleDataState) {
                is UiState.Success -> {
                    val edible = edibleDataState.data?.edible

                    if (edible != null) {
                        ByBarcodeFound(
                            edible = edible,
                            isUserPremium = isUserPremium,
                            currentLogCategory = currentLogCategory,
                            onLog = { logEntry -> onLog(edible, logEntry) },
                            onDismiss = onDismiss
                        )
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

@Composable
private fun ByBarcodeFound(
    edible: AppEdible,
    currentLogCategory: FoodLogCategory,
    isUserPremium: Boolean,
    onLog: (FoodLogCategory?) -> Unit,
    onDismiss: () -> Unit
) {
    val edibleComposables = remember(edible.id) {
        UFood.FoodInformationComposables(edible.information, isUserPremium)
    }

    val nutrientsScrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .areaContainer(
                borderColor = MaterialTheme.colorScheme.surfaceVariant
            )
            .consumeTap()
    ) {
        edibleComposables.BaseInformation()

        edibleComposables.NutrientSummary(withWrap = false)

        Column(
            modifier = Modifier
                .verticalScroll(nutrientsScrollState)
                .weight(1f)
        ) {
            edibleComposables.RemainingNutrients(withWrap = false)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentLogCategory.toEdibleType() != edible.information.type) {
                when (edible.information.type) {
                    EdibleType.FOOD -> Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FoodLogCategory.entries.forEach { logEntry ->
                            if (logEntry != FoodLogCategory.SUPPLEMENT) {
                                Button(
                                    contentPadding = PaddingValues(horizontal = 0.dp),
                                    onClick = { onLog(logEntry) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Log as \n${logEntry.toString().toTitleCase()}",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = robotoSerifFamily
                                    )
                                }
                            }
                        }
                    }

                    EdibleType.SUPPLEMENT -> Button(
                        onClick = { onLog(FoodLogCategory.SUPPLEMENT) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Log as Supplement",
                            fontFamily = robotoSerifFamily
                        )
                    }
                }

            } else Button(
                onClick = { onLog(null) },
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
}