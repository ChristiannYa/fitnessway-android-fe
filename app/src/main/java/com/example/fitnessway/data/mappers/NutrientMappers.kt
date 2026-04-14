package com.example.fitnessway.data.mappers

import com.example.fitnessway.constants.NutrientId
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientAmountWithColor
import com.example.fitnessway.data.model.m_26.NutrientBase
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.data.model.m_26.NutrientGroupable
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.NutrientPreferences
import com.example.fitnessway.data.model.m_26.NutrientPreview
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.toEnum

fun NutrientPreview.toList() =
    listOf(this.calories, this.carbs, this.fats, this.protein)

fun <N : NutrientGroupable> NutrientsByType<N>.toList() =
    this.basic + this.vitamins + this.minerals

fun <N : NutrientGroupable> NutrientsByType<N>.toTypedList() =
    listOf(
        NutrientType.BASIC to this.basic,
        NutrientType.VITAMIN to this.vitamins,
        NutrientType.MINERAL to this.minerals
    )

fun <N : NutrientGroupable> NutrientsByType<N>.mapnbt(
    transform: (
        type: NutrientType,
        nutrients: List<N>
    ) -> List<N>
) = NutrientsByType(
    basic = transform(NutrientType.BASIC, this.basic),
    vitamins = transform(NutrientType.VITAMIN, this.vitamins),
    minerals = transform(NutrientType.MINERAL, this.minerals)
)

fun <N : NutrientGroupable> List<N>.toType(): NutrientsByType<N> =
    this
        .groupBy { it.iNutrientType }
        .let {
            NutrientsByType(
                basic = it[NutrientType.BASIC] ?: emptyList(),
                vitamins = it[NutrientType.VITAMIN] ?: emptyList(),
                minerals = it[NutrientType.MINERAL] ?: emptyList()
            )
        }

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

fun NutrientsByType<NutrientDataAmount>.toNutrientPreview() =
    this.basic.let { nutrients ->
        NutrientPreview(
            calories = nutrients
                .find { it.nutrientData.base.id == NutrientId.CALORIES }
                .toNutrientAmountWithColorOrNull(),
            carbs = nutrients
                .find { it.nutrientData.base.id == NutrientId.CARBS }
                .toNutrientAmountWithColorOrNull(),
            fats = nutrients
                .find { it.nutrientData.base.id == NutrientId.FATS }
                .toNutrientAmountWithColorOrNull(),
            protein = nutrients
                .find { it.nutrientData.base.id == NutrientId.PROTEIN }
                .toNutrientAmountWithColorOrNull()
        )
    }

fun NutrientDataAmount.toNutrientAmountWithColor() =
    NutrientAmountWithColor(
        amount = this.amount,
        color = this.nutrientData.preferences.hexColor
    )

fun NutrientDataAmount?.toNutrientAmountWithColorOrNull() =
    this?.toNutrientAmountWithColor() ?: NutrientAmountWithColor()

fun MNutrient.Model.NutrientDataWithAmount.toM26NutrientAmountWithColor() =
    NutrientAmountWithColor(
        amount = this.amount,
        color = this.nutrientWithPreferences.preferences.hexColor
    )

fun MNutrient.Model.NutrientDataWithAmount?.toM26NutrientAmountWithColorOrNull() =
    this?.toM26NutrientAmountWithColor() ?: NutrientAmountWithColor()

fun MNutrient.Model.NutrientDataWithAmount.toM26NutrientInFood() =
    NutrientDataAmount(
        nutrientData = NutrientData(
            base = NutrientBase(
                id = this.nutrientWithPreferences.nutrient.id,
                name = this.nutrientWithPreferences.nutrient.name,
                unit = this.nutrientWithPreferences.nutrient.unit.toEnum(),
                type = this.nutrientWithPreferences.nutrient.type,
                symbol = this.nutrientWithPreferences.nutrient.symbol,
                isPremium = this.nutrientWithPreferences.nutrient.isPremium
            ),
            preferences = NutrientPreferences(
                goal = this.nutrientWithPreferences.preferences.goal,
                hexColor = this.nutrientWithPreferences.preferences.hexColor
            )
        ),
        amount = this.amount
    )

fun MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>.toM26NutrientsInFood() =
    NutrientsByType<NutrientDataAmount>(
        basic = this.basic.map { it.toM26NutrientInFood() },
        vitamins = this.vitamin.map { it.toM26NutrientInFood() },
        minerals = this.mineral.map { it.toM26NutrientInFood() }
    )

fun MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>.toNutrientPreview() =
    this.basic.let { nutrients ->
        NutrientPreview(
            calories = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.CALORIES }
                .toM26NutrientAmountWithColorOrNull(),
            carbs = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.CARBS }
                .toM26NutrientAmountWithColorOrNull(),
            fats = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.FATS }
                .toM26NutrientAmountWithColorOrNull(),
            protein = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.PROTEIN }
                .toM26NutrientAmountWithColorOrNull()
        )
    }


fun NutrientsByType<NutrientDataAmount>.toM25NutrientsInFood() =
    MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>(
        basic = this.basic.map { it.toM25NutrientDataWithAmount() },
        vitamin = this.vitamins.map { it.toM25NutrientDataWithAmount() },
        mineral = this.minerals.map { it.toM25NutrientDataWithAmount() }
    )

fun NutrientDataAmount.toM25NutrientDataWithAmount() = MNutrient.Model.NutrientDataWithAmount(
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