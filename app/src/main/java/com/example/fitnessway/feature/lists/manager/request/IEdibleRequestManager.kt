package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.edible.creation.IEdibleCreation
import kotlinx.coroutines.flow.StateFlow

interface IEdibleRequestManager : IEdibleCreation {
    val pendingFood: StateFlow<PendingFood?>
    val reviewIdToDismiss: StateFlow<Int?>

    fun setPendingFood(food: PendingFood)
    fun setReviewIdToRemove(id: Int)
}