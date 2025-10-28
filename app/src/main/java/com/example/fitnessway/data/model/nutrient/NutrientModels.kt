package com.example.fitnessway.data.model.nutrient

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NutrientType {
    @SerialName("basic")
    BASIC,

    @SerialName("vitamin")
    VITAMIN,

    @SerialName("mineral")
    MINERAL
}

@Serializable
data class Nutrient(
    val id: Int,
    val name: String,
    val symbol: String?,
    val unit: String,
    val type: NutrientType,

    @SerialName("is_premium")
    val isPremium: Boolean
)

@Serializable
data class NutrientIntake(
    val nutrient: Nutrient,
    val intake: Float,
    val goal: Float?
)

@Serializable
data class NutrientsByType(
    val basic: List<NutrientIntake>,
    val vitamin: List<NutrientIntake>,
    val mineral: List<NutrientIntake>
)

@Serializable
data class NutrientsByTypeApiResponse(
    @SerialName("nutrient_intakes")
    val nutrientIntakes: NutrientsByType
)

typealias NutrientsByTypeFetchResponse = ApiResponseWithContent<NutrientsByTypeApiResponse>