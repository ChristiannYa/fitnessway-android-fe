package com.example.fitnessway.feature.lists.manager.request

import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.PendingEdible
import com.example.fitnessway.util.edible.creation.EdibleCreation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EdibleRequestManager : IEdibleRequestManager, EdibleCreation(EdibleSource.APP) {
    private val _pendingEdible = MutableStateFlow<PendingEdible?>(null)
    override val pendingEdible: StateFlow<PendingEdible?> = _pendingEdible

    private val _reviewIdToDismiss = MutableStateFlow<Int?>(null)
    override val reviewIdToDismiss: StateFlow<Int?> = _reviewIdToDismiss

    override fun setPendingFood(food: PendingEdible) {
        _pendingEdible.value = food
    }

    override fun setReviewIdToRemove(id: Int) {
        _reviewIdToDismiss.value = id
    }
}