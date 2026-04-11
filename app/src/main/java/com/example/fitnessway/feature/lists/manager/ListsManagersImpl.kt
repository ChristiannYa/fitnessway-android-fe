package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.creation.CreationManager
import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.request.FoodRequestManager

class ListsManagersImpl(
    override val edition: EditionManager,
    override val creation: CreationManager,
    override val request: FoodRequestManager
) : IListsManagers