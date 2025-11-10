package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.food.FoodManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager

class HomeManagersImpl (
    override val date: DateManager,
    override val foodLog: FoodLogManager,
    override val food: FoodManager,
) : IHomeManagers