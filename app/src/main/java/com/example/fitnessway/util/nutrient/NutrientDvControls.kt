package com.example.fitnessway.util.nutrient

import com.example.fitnessway.util.logcat
import kotlinx.coroutines.flow.MutableStateFlow

open class NutrientDvControls : INutrientDvControls {
    private val _nutrientDvMap = MutableStateFlow<Map<Int, String>>(emptyMap())

    private fun addNutrientValueToMap(nutrientId: Int, value: String) {
        logcat("addNutrientValueToMap(#$nutrientId: $value)")
        _nutrientDvMap.value = _nutrientDvMap.value.toMutableMap().apply { put(nutrientId, value) }
    }

    private fun removeNutrientValueFromMap(nutrientId: Int) {
        _nutrientDvMap.value = _nutrientDvMap.value.toMutableMap().apply { remove(nutrientId) }
    }

    private fun resetMap() {
        _nutrientDvMap.value = emptyMap()
    }

    override val controls = INutrientDvControls.NutrientDvControls(
        nutrientDvMap = _nutrientDvMap,
        onAdd = ::addNutrientValueToMap,
        onRemove = ::removeNutrientValueFromMap,
        onClearData = ::resetMap
    )
}