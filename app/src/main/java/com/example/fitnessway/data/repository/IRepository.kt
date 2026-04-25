package com.example.fitnessway.data.repository

import kotlinx.coroutines.flow.StateFlow

interface IRepository<T> {
    val uiState: StateFlow<T>

    fun update(update: (T) -> T)
    fun clear()
}