package com.example.fitnessway.feature.home.manager.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UiManager : IUiManager {
    private val _areFoodLogsVisible = MutableStateFlow(false)
    override val areFoodLogsVisible: StateFlow<Boolean> = _areFoodLogsVisible

    override fun toggleFoodLogsVisibility() {
        _areFoodLogsVisible.value = !_areFoodLogsVisible.value
    }
}