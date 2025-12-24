package com.example.fitnessway.feature.home.manager.ui

import kotlinx.coroutines.flow.StateFlow

interface IUiManager {
    val areFoodLogsVisible: StateFlow<Boolean>

    fun toggleFoodLogsVisibility()
}