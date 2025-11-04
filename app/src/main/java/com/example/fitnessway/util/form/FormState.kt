package com.example.fitnessway.util.form

data class FormState<T>(
    val data: T,
    var isEditing: Boolean = false,
    private val originalData: T? = null
) {
    fun startEdit(): FormState<T> {
        return copy(
            isEditing = true,
            originalData = data
        )
    }

    fun cancelEdit(): FormState<T> {
        return copy(
            isEditing = false,
            data = originalData ?: data
        )
    }

    fun saveEdit(): FormState<T> {
        return copy(
            isEditing = false,
            originalData = null
        )
    }
}