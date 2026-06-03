package com.example.fitnessway.util.nutrient

import com.example.fitnessway.data.model.m_26.NutrientBase
import kotlinx.coroutines.flow.StateFlow

interface INutrientDvControls {
    data class NutrientDvControls(
        val nutrientDvMap: StateFlow<Map<Int, String>>,
        val onAdd: (nutrientId: Int, amount: String) -> Unit,
        val onRemove: (nutrientId: Int) -> Unit,
        val onClearData: () -> Unit,
    )

    data class NutrientDvState(
        val nutrient: NutrientBase?,
        val isInNutrientDvMap: Boolean,
        val nutrientDvControls: NutrientDvControls?
    )

    val dvControls: NutrientDvControls
}