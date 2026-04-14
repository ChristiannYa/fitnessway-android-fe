package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.food.creation.FoodCreation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodRequestManager : IFoodRequestManager, FoodCreation(FoodSource.APP) {
    private val _pendingFood = MutableStateFlow<PendingFood?>(null)
    override val pendingFood: StateFlow<PendingFood?> = _pendingFood

    override fun setPendingFood(food: PendingFood) {
        _pendingFood.value = food
    }
}