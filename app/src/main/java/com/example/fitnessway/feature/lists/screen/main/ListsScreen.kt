package com.example.fitnessway.feature.lists.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.FoodsList
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.screen.main.composables.ToggleListViewButtons
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    listsViewModel: ListsViewModel = koinViewModel()
) {
    val uiState by listsViewModel.uiState.collectAsState()
    val selectedList by listsViewModel.selectedList.collectAsState()

    LaunchedEffect(Unit) {
        listsViewModel.getFoods()
    }

    Screen(
        isMainScreen = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                ToggleListViewButtons(
                    onToggleSelectedList = listsViewModel::setSelectedList,
                    selectedOption = selectedList
                )

                when (selectedList) {
                    ListOption.Food -> {
                        FoodsList(
                            state = uiState.foodsState
                        )
                    }

                    ListOption.Supplement -> {
                        SupplementList()
                    }
                }
            }
        )

    }
}