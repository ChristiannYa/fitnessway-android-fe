package com.example.fitnessway.feature.home.manager

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager
import com.example.fitnessway.feature.home.manager.foodrequest.FoodRequestManager
import com.example.fitnessway.feature.home.manager.ui.UiManager

interface IHomeManager {
    val date: DateManager
    val foodLog: FoodLogManager
    val foodRequest: FoodRequestManager
    val ui: UiManager
}