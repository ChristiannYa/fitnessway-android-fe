package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager
import com.example.fitnessway.feature.home.manager.ui.UiManager

class HomeManagerImpl(
    override val date: DateManager,
    override val foodLog: FoodLogManager,
    override val ui: UiManager
) : IHomeManager