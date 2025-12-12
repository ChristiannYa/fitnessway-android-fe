package com.example.fitnessway.feature.lists.screen.main

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.screen.main.composables.ToggleListViewButtons
import com.example.fitnessway.feature.lists.screen.main.composables.foodsList
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewDetails: () -> Unit,
) {
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    Screen(
        isMainScreen = true
    ) {
        CompositionLocalProvider(
            values = arrayOf(LocalOverscrollFactory provides null),
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight(),
                    content = {
                        stickyHeader {
                            ToggleListViewButtons(
                                onToggleSelectedList = viewModel::setSelectedList,
                                selectedOption = selectedList
                            )
                        }

                        when (selectedList) {
                            ListOption.Food -> {
                                foodsList(
                                    state = foodRepoUiState.foodsUiState,
                                    onViewDetails = { food ->
                                        viewModel.setSelectedFood(food)
                                        onViewDetails()
                                    }
                                )
                            }

                            ListOption.Supplement -> {
                                item {
                                    SupplementList()
                                }
                            }
                        }
                    }
                )
            }
        )
    }
}