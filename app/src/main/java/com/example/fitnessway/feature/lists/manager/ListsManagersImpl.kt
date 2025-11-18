package com.example.fitnessway.feature.lists.manager

import com.example.fitnessway.feature.lists.manager.edition.EditionManager
import com.example.fitnessway.feature.lists.manager.toggle.SelectionManager

class ListsManagersImpl (
    override val selection: SelectionManager,
    override val edition: EditionManager
) : IListsManagers