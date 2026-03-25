package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientGroupable
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.NutrientInFood
import com.example.fitnessway.data.model.m_26.NutrientPreview
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.UNutrient

fun <N : NutrientGroupable> NutrientsByType<N>.toList() =
    this.basic + this.vitamins + this.minerals

fun <N : NutrientGroupable> List<N>.toType(): NutrientsByType<N> {
    val grouped = this.groupBy { it.nutrientType }

    return NutrientsByType(
        basic = grouped[NutrientType.BASIC] ?: emptyList(),
        vitamins = grouped[NutrientType.VITAMIN] ?: emptyList(),
        minerals = grouped[NutrientType.MINERAL] ?: emptyList()
    )
}

fun NutrientPreview.toList() =
    listOf(this.calories, this.carbs, this.fats, this.protein)

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

fun NutrientInFood.toM25NutrientDataWithAmount() = MNutrient.Model.NutrientDataWithAmount(
    nutrientWithPreferences = MNutrient.Model.NutrientWithPreferences(
        nutrient = MNutrient.Model.Nutrient(
            id = this.nutrientData.base.id,
            name = this.nutrientData.base.name,
            symbol = this.nutrientData.base.symbol,
            unit = this.nutrientData.base.unit.toString(),
            type = this.nutrientData.base.type,
            isPremium = this.nutrientData.base.isPremium
        ),
        preferences = MNutrient.Model.NutrientPreferences(
            goal = this.nutrientData.preferences.goal,
            hexColor = this.nutrientData.preferences.hexColor
        ),
    ),
    amount = this.amount
)

fun NutrientsByType<NutrientInFood>.toM25NutrientsInFood() =
    MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>(
        basic = this.basic.map { it.toM25NutrientDataWithAmount() },
        vitamin = this.vitamins.map { it.toM25NutrientDataWithAmount() },
        mineral = this.minerals.map { it.toM25NutrientDataWithAmount() }
    )