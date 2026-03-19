package com.example.fitnessway.util

import com.example.fitnessway.data.model.MNutrient
import kotlinx.coroutines.flow.StateFlow

interface INutrientDvControls {
    data class NutrientDvControls(
        val nutrientDvMap: StateFlow<Map<Int, String>>,
        val onAdd: (nutrientId: Int, amount: String) -> Unit,
        val onRemove: (nutrientId: Int) -> Unit,
        val onClearData: () -> Unit,
    )

    data class NutrientDvState(
        val nutrient: MNutrient.Model.Nutrient?,
        val isInNutrientDvMap: Boolean,
        val nutrientDvControls: NutrientDvControls?
    )

    val nutrientDvControls: NutrientDvControls
}