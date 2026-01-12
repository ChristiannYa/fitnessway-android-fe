package com.example.fitnessway.feature.lists.manager.toggle

import com.example.fitnessway.data.model.MFood.Enum.ListOption
import kotlinx.coroutines.flow.StateFlow

interface ISelectionManager {
    val selectedList: StateFlow<ListOption>

    fun setSelectedList(list: ListOption)
}