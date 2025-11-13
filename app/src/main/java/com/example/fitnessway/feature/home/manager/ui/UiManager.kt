package com.example.fitnessway.feature.home.manager.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UiManager : IUiManager {
    private val _isCreateMenuVisible = MutableStateFlow(false)
    override val isCreateMenuVisible: StateFlow<Boolean> = _isCreateMenuVisible

    override fun toggleCreateMenuVisibility() {
        _isCreateMenuVisible.value = !_isCreateMenuVisible.value
    }
}