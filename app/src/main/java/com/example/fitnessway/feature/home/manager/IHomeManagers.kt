package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.food.FoodManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager

interface IHomeManagers {
    val date: DateManager
    val foodLog: FoodLogManager
    val food: FoodManager
}