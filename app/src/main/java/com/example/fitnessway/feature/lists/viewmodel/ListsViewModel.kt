package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.toggle.ISelectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val foodRepo: IFoodRepository,
    managers: IListsManagers
) : ViewModel(),
    ISelectionManager by managers.selection,
    IEditionManager by managers.edition {

    private val _uiState = MutableStateFlow(ListsScreenUiState())
    val uiState: StateFlow<ListsScreenUiState> = _uiState.asStateFlow()

    fun getFoods() {
        viewModelScope.launch {
            foodRepo.getFoods().collect { state ->
                _uiState.update { it.copy(foodsState = state) }
            }
        }
    }
}