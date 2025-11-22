package com.example.fitnessway.util.form

data class FormState<T>(
    val data: T,
    var isEditing: Boolean = false,
    private val originalData: T? = null
) {
    fun edit(): FormState<T> {
        return copy(
            isEditing = true,
            originalData = data
        )
    }

    fun cancel(): FormState<T> {
        return copy(
            isEditing = false,
            data = originalData ?: data
        )
    }

    fun save(): FormState<T> {
        return copy(
            isEditing = false,
            originalData = null
        )
    }

    fun setIsEditingToFalse(): FormState<T> {
        return copy(
            isEditing = false
        )
    }
}