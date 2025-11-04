package com.example.fitnessway.util.form

import android.util.Log
import com.example.fitnessway.util.Constants

data class FormState<T>(
    val data: T,
    var isEditing: Boolean = false,
    private val originalData: T? = null
) {
    fun edit(): FormState<T> {
        Log.d(Constants.DEBUG_TAG, "startEdit called")
        return copy(
            isEditing = true,
            originalData = data
        )
    }

    fun cancel(): FormState<T> {
        Log.d(Constants.DEBUG_TAG, "cancelEdit called")
        return copy(
            isEditing = false,
            data = originalData ?: data
        )
    }

    fun save(): FormState<T> {
        Log.d(Constants.DEBUG_TAG, "saveEdit called")
        return copy(
            isEditing = false,
            originalData = null
        )
    }
}