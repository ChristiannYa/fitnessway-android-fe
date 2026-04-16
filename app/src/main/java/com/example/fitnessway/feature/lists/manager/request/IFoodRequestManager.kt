package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.food.creation.IFoodCreation
import kotlinx.coroutines.flow.StateFlow

interface IFoodRequestManager : IFoodCreation {
    val pendingFood: StateFlow<PendingFood?>
    val reviewIdToDismiss: StateFlow<Int?>

    fun setPendingFood(food: PendingFood)
    fun setReviewIdToRemove(id: Int)
}