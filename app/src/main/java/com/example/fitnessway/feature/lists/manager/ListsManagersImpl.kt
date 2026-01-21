package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.creation.CreationManager
import com.example.fitnessway.feature.lists.manager.edition.EditionManager

class ListsManagersImpl(
    override val edition: EditionManager,
    override val food: CreationManager
) : IListsManagers