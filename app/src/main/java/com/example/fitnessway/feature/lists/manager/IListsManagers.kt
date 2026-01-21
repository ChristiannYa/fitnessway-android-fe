package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.creation.CreationManager
import com.example.fitnessway.feature.lists.manager.edition.EditionManager

interface IListsManagers {
    val edition: EditionManager
    val food: CreationManager
}