package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.PendingEdible
import com.example.fitnessway.util.edible.creation.IEdibleCreation
import kotlinx.coroutines.flow.StateFlow

interface IEdibleRequestManager : IEdibleCreation {
    val pendingEdible: StateFlow<PendingEdible?>
    val reviewIdToDismiss: StateFlow<Int?>

    fun setPendingFood(food: PendingEdible)
    fun setReviewIdToRemove(id: Int)
}