package com.example.fitnessway.data.model.m_26

import com.example.fitnessway.util.UNutrient.NUTRIENT_IDS_WITH_DV
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
    val byId: Int
    val byType: NutrientType
    val bySortOrder: Int
}

typealias NutrientIntakes = NutrientsByType<NutrientDataAmount>

@Serializable
data class NutrientsByType<N : NutrientGroupable>(
    val basic: List<N>,
    val vitamin: List<N>,
    val mineral: List<N>
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
) : NutrientGroupable {
    val hasDailyValue by lazy { this.id in NUTRIENT_IDS_WITH_DV }

    override val byId: Int
        get() = this.id

    override val byType: NutrientType
        get() = this.type

    override val bySortOrder: Int
        get() = 0
}

@Serializable
data class NutrientPreferences(
    val hexColor: String? = null,
    val goal: Double? = null
)

@Serializable
data class NutrientConfiguration(
    val parentId: Int? = null,
    val sortOrder: Int
)

@Serializable
data class NutrientData(
    val base: NutrientBase,
    val preferences: NutrientPreferences,
    val configuration: NutrientConfiguration
) : NutrientGroupable {

    override val byId: Int
        get() = this.base.id

    override val byType: NutrientType
        get() = this.base.type

    override val bySortOrder: Int
        get() = this.configuration.sortOrder
}

@Serializable
data class NutrientDataAmount(
    val data: NutrientData,
    val amount: Double
) : NutrientGroupable {

    override val byId: Int
        get() = this.data.base.id

    override val byType: NutrientType
        get() = this.data.base.type

    override val bySortOrder: Int
        get() = this.data.configuration.sortOrder
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
data class NutrientsByTypeResponse(
    val nutrientsByType: NutrientsByType<NutrientData>
)

@Serializable
data class NutrientIntakesResponse(
    val nutrientIntakes: NutrientIntakes
)