package com.example.fitnessway.feature.home.screen.foodselection

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MFood.Enum.FoodSort
import com.example.fitnessway.feature.home.screen.foodselection.composables.Foods
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.Formatters.snakeToReadableText
import com.example.fitnessway.util.UFood.Ui.getFoodLogCategory
import org.koin.compose.viewmodel.koinViewModel
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource
import com.example.fitnessway.util.UiState

@Composable
fun FoodSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onSelectedFoodToLog: () -> Unit
) {
    val foodUiState by viewModel.foodRepoUiState.collectAsState()
    val foodLogCategory by viewModel.foodLogCategory.collectAsState()

    val user = viewModel.user

    LaunchedEffect(Unit) {
        viewModel.getFoods()
        viewModel.getFoodSort()
    }

    if (user != null) {
        val foodLogCategoryCopy = foodLogCategory

        if (foodLogCategoryCopy != null) {
            val moreOptionsState = Structure.rememberMoreOptionsState()
            val categoryString = getFoodLogCategory(foodLogCategoryCopy)

            Screen(
                header = {
                    Header(
                        onBackClick = onBackClick,
                        title = "$categoryString selection"
                    ) {
                        when (val state = foodUiState.foodSortUiState) {
                            is UiState.Success -> {
                                Clickables.AppIconButton(
                                    onClick = moreOptionsState::toggle,
                                    contentDescription = "Filter sort display",
                                    icon = AppIconButtonSource.Vector(Icons.Default.FilterList)
                                )
                            }

                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            else -> {}
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Foods(
                        state = foodUiState.foodsUiState,
                        setSelectedFoodToLog = viewModel::setSelectedFoodToLog,
                        onSelectedFoodToLog,
                        user = user
                    )

                    if (foodUiState.foodSortUiState is UiState.Success) {
                        DarkOverlay(
                            isVisible = moreOptionsState.isVisible,
                            onClick = {
                                moreOptionsState.hide()
                            }
                        )

                        Structure.MoreOptions(
                            state = moreOptionsState,
                            *enumValues<FoodSort>().map { sortType ->
                                Structure.MoreOptionsConfig(
                                    text = sortType.name.lowercase().snakeToReadableText(),
                                    icon = sortType.icon,
                                    iconModifier = Modifier.size(18.dp),
                                    onClick = {
                                        logcat("$sortType")
                                    }
                                )
                            }.toTypedArray(),
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }

        } else NotFoundMessage("Food log category not found")

    } else NotFoundMessage("Data not found")
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FoodSelectionPreview() {
    FitnesswayTheme {
        FoodSelectionScreen(
            onBackClick = {},
            onSelectedFoodToLog = {}
        )
    }
}