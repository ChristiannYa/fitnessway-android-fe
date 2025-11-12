package com.example.fitnessway.feature.home.manager.ui

import kotlinx.coroutines.flow.StateFlow

interface IUiManager {
    val isCreateMenuVisible: StateFlow<Boolean>

    fun toggleCreateMenuVisibility()
}