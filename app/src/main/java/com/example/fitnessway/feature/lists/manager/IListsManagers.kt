package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.creation.ICreationManager
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager

interface IListsManagers {
    val edition: IEditionManager
    val creation: ICreationManager
}