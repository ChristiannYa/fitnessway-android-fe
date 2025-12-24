package com.example.fitnessway.feature.lists.screen.main

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.food.ListOption
import com.example.fitnessway.feature.lists.screen.main.composables.SupplementList
import com.example.fitnessway.feature.lists.screen.main.composables.ToggleListViewButtons
import com.example.fitnessway.feature.lists.screen.main.composables.foodsList
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.WhiteBackground
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListsScreen(
    viewModel: ListsViewModel = koinViewModel(),
    onViewFoodDetails: () -> Unit,
    onNavigateToFoodCreationForm: () -> Unit
) {
    val foodRepoUiState by viewModel.foodRepoUiState.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFoods()
    }

    Screen(
        isMainScreen = true
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(values = arrayOf(LocalOverscrollFactory provides null)) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
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
                                    onViewFoodDetails()
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
            }

            IconButton(
                onClick = onNavigateToFoodCreationForm,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                )
            }
        }
    }
}