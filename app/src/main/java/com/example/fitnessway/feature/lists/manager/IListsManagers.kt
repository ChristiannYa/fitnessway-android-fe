package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.creation.CreationManager
import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.food.FoodManager

interface IListsManagers {
    val edition: EditionManager
    val creation: CreationManager
    val food: FoodManager
}