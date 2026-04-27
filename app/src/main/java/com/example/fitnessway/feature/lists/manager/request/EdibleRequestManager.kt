package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.util.edible.creation.EdibleCreation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EdibleRequestManager : IEdibleRequestManager, EdibleCreation(EdibleSource.APP) {
    private val _pendingFood = MutableStateFlow<PendingFood?>(null)
    override val pendingFood: StateFlow<PendingFood?> = _pendingFood

    private val _reviewIdToDismiss = MutableStateFlow<Int?>(null)
    override val reviewIdToDismiss: StateFlow<Int?> = _reviewIdToDismiss

    override fun setPendingFood(food: PendingFood) {
        _pendingFood.value = food
    }

    override fun setReviewIdToRemove(id: Int) {
        _reviewIdToDismiss.value = id
    }
}