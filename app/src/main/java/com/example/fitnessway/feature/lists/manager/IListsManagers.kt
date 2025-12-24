package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.food.FoodManager
import com.example.fitnessway.feature.lists.manager.toggle.SelectionManager

interface IListsManagers {
    val selection: SelectionManager
    val edition: EditionManager
    val food: FoodManager
}