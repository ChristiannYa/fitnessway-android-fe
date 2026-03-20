package com.example.fitnessway.mappers

import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.util.UNutrient

fun Map<Int, String>.toNutrientIdAmountList(
    nutrientDvMap: Map<Int, String>? = null
): List<NutrientIdWithAmount> =
    this.filter { (it.value.toDoubleOrNull() ?: 0.0) > 0 }
        .map {
            val nutrientId = it.key
            val amountLiteral = it.value.toDoubleOrNull() ?: 0.0

            val amount = nutrientDvMap?.let { dvMap ->
                if (dvMap.containsKey(nutrientId)) {
                    UNutrient.percentDvToNutrientAmount(nutrientId, amountLiteral)
                } else amountLiteral
            } ?: amountLiteral

            NutrientIdWithAmount(nutrientId, amount)
        }