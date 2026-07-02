package com.example.fitnessway.data.mappers

import com.example.fitnessway.constants.NutrientId
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientAmountWithColor
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.data.model.m_26.NutrientGroupable
import com.example.fitnessway.data.model.m_26.NutrientIdWithAmount
import com.example.fitnessway.data.model.m_26.NutrientPreview
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.nutrient.getNutrientDv

fun NutrientPreview.toList() =
    listOf(this.calories, this.carbs, this.fats, this.protein)

fun <N : NutrientGroupable> NutrientsByType<N>.toList() =
    this.basic + this.vitamin + this.mineral

fun <N : NutrientGroupable> NutrientsByType<N>.toTypedList() =
    listOf(
        NutrientType.BASIC to this.basic,
        NutrientType.VITAMIN to this.vitamin,
        NutrientType.MINERAL to this.mineral
    )

fun <N : NutrientGroupable> NutrientsByType<N>.mapnbt(
    transform: (
        type: NutrientType,
        nutrients: List<N>
    ) -> List<N>
) = NutrientsByType(
    basic = transform(NutrientType.BASIC, this.basic),
    vitamin = transform(NutrientType.VITAMIN, this.vitamin),
    mineral = transform(NutrientType.MINERAL, this.mineral)
)

fun <N : NutrientGroupable> List<N>.toType(): NutrientsByType<N> =
    this
        .groupBy { it.byType }
        .let {
            NutrientsByType(
                basic = it[NutrientType.BASIC] ?: emptyList(),
                vitamin = it[NutrientType.VITAMIN] ?: emptyList(),
                mineral = it[NutrientType.MINERAL] ?: emptyList()
            )
        }

fun <N : NutrientGroupable> NutrientsByType<N>.toListByType(type: NutrientType) =
    when (type) {
        NutrientType.BASIC -> this.basic
        NutrientType.VITAMIN -> this.vitamin
        NutrientType.MINERAL -> this.mineral
    }

fun Map<Int, String>.toNutrientDvList(
    nutrientDvMap: Map<Int, String>
): List<NutrientIdWithAmount> =
    this.filter { (it.value.toDoubleOrNull() ?: 0.0) > 0 }
        .map {
            val nutrientId = it.key
            val amountLiteral = it.value.toDoubleOrNull() ?: 0.0

            val amount = if (nutrientDvMap.containsKey(nutrientId)) {
                getNutrientDv(nutrientId, amountLiteral)
            } else amountLiteral

            NutrientIdWithAmount(nutrientId, amount)
        }

fun NutrientsByType<NutrientDataAmount>.toNutrientPreview() =
    this.basic.let { nutrients ->
        NutrientPreview(
            calories = nutrients
                .find { it.data.base.id == NutrientId.Base.CALS }
                .toNutrientAmountWithColorOrNull(),
            carbs = nutrients
                .find { it.data.base.id == NutrientId.Base.CARBS }
                .toNutrientAmountWithColorOrNull(),
            fats = nutrients
                .find { it.data.base.id == NutrientId.Base.FATS }
                .toNutrientAmountWithColorOrNull(),
            protein = nutrients
                .find { it.data.base.id == NutrientId.Base.PROTEIN }
                .toNutrientAmountWithColorOrNull()
        )
    }

fun NutrientDataAmount.toNutrientAmountWithColor() =
    NutrientAmountWithColor(
        amount = this.amount,
        color = this.data.preferences.hexColor
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


fun MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>.toNutrientPreview() =
    this.basic.let { nutrients ->
        NutrientPreview(
            calories = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.Base.CALS }
                .toM26NutrientAmountWithColorOrNull(),
            carbs = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.Base.CARBS }
                .toM26NutrientAmountWithColorOrNull(),
            fats = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.Base.FATS }
                .toM26NutrientAmountWithColorOrNull(),
            protein = nutrients
                .find { it.nutrientWithPreferences.nutrient.id == NutrientId.Base.PROTEIN }
                .toM26NutrientAmountWithColorOrNull()
        )
    }


fun NutrientsByType<NutrientDataAmount>.toM25NutrientsInFood() =
    MNutrient.Model.NutrientsByType<MNutrient.Model.NutrientDataWithAmount>(
        basic = this.basic.map { it.toM25NutrientDataWithAmount() },
        vitamin = this.vitamin.map { it.toM25NutrientDataWithAmount() },
        mineral = this.mineral.map { it.toM25NutrientDataWithAmount() }
    )

fun NutrientDataAmount.toM25NutrientDataWithAmount() = MNutrient.Model.NutrientDataWithAmount(
    nutrientWithPreferences = MNutrient.Model.NutrientWithPreferences(
        nutrient = MNutrient.Model.Nutrient(
            id = this.data.base.id,
            name = this.data.base.name,
            symbol = this.data.base.symbol,
            unit = this.data.base.unit.toString(),
            type = this.data.base.type,
            isPremium = this.data.base.isPremium
        ),
        preferences = MNutrient.Model.NutrientPreferences(
            goal = this.data.preferences.goal,
            hexColor = this.data.preferences.hexColor
        ),
    ),
    amount = this.amount
)