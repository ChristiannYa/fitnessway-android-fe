package com.example.fitnessway.data.model.m_26

import kotlinx.serialization.Serializable

/**
 * Represents nutrient information that can be grouped by [NutrientType]
 */
interface NutrientGroupable {
    val nutrientType: NutrientType
}

@Serializable
enum class NutrientType {
    BASIC,
    VITAMIN,
    MINERAL,
}

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
    val name: String,
    val unit: ServingUnit,
    val type: NutrientType,
    val symbol: String? = null,
    val isPremium: Boolean
)

@Serializable
data class NutrientPreferences(
    val hexColor: String? = null,
    val goal: Double? = null
)

@Serializable
data class NutrientData(
    val base: NutrientBase,
    val preferences: NutrientPreferences
) : NutrientGroupable {
    override val nutrientType: NutrientType
        get() = this.base.type
}

@Serializable
data class NutrientInFood(
    val nutrientData: NutrientData,
    val amount: Double
) : NutrientGroupable {
    override val nutrientType: NutrientType
        get() = this.nutrientData.nutrientType
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