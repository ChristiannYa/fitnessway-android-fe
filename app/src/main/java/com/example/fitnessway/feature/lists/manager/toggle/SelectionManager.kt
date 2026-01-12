package com.example.fitnessway.feature.lists.manager.toggle

import com.example.fitnessway.data.model.MFood.Enum.ListOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectionManager : ISelectionManager {
    private val _selectedList = MutableStateFlow(ListOption.Food)
    override val selectedList: StateFlow<ListOption> = _selectedList

    override fun setSelectedList(list: ListOption) {
        when (list) {
            ListOption.Food -> _selectedList.value = ListOption.Food
            ListOption.Supplement -> _selectedList.value = ListOption.Supplement
        }
    }
}