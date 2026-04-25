package com.example.fitnessway.data.model.m_26

import kotlinx.serialization.Serializable

@Serializable
enum class NutrientType {
    BASIC,
    VITAMIN,
    MINERAL,
}

enum class NutrientIntakeMath {
    ADD,
    SUBTRACT
}

/**
 * Represents nutrient information that can be grouped by [NutrientType]
 */
interface NutrientGroupable {
    val iNutrientType: NutrientType
}

typealias NutrientIntakes = NutrientsByType<NutrientDataAmount>

@Serializable
data class NutrientsByType<N : NutrientGroupable>(
    val basic: List<N>,
    val vitamins: List<N>,
    val minerals: List<N>
)

@Serializable
data class NutrientPreview(
    val calories: NutrientAmountWithColor,
    val carbs: NutrientAmountWithColor,
    val fats: NutrientAmountWithColor,
    val protein: NutrientAmountWithColor
)

@Serializable
data class NutrientBase(
    val id: Int,
    val name: kotlin.String,
    val unit: ServingUnit,
    val type: NutrientType,
    val symbol: kotlin.String? = null,
    val isPremium: Boolean
)

@Serializable
data class NutrientPreferences(
    val hexColor: kotlin.String? = null,
    val goal: Double? = null
)

@Serializable
data class NutrientData(
    val base: NutrientBase,
    val preferences: NutrientPreferences
) : NutrientGroupable {
    override val iNutrientType: NutrientType
        get() = this.base.type
}

@Serializable
data class NutrientDataAmount(
    val nutrientData: NutrientData,
    val amount: Double
) : NutrientGroupable {
    override val iNutrientType: NutrientType
        get() = this.nutrientData.base.type
}

@Serializable
data class NutrientIdWithAmount(
    val nutrientId: Int,
    val amount: Double
)

@Serializable
data class NutrientAmountWithColor(
    val amount: Double? = null,
    val color: String? = null
)

@Serializable
data class NutrientIntakesResponse(
    val nutrientIntakes: NutrientIntakes
)