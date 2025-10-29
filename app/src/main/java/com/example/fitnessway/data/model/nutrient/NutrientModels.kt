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
data class NutrientsByType<T>(
    val basic: List<T>,
    val vitamin: List<T>,
    val mineral: List<T>
)

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
    val intake: Double,
    val goal: Double?
)

typealias NutrientIntakesByType = NutrientsByType<NutrientIntake>

@Serializable
data class NutrientsByTypeApiResponse(
    @SerialName("nutrient_intakes")
    val nutrientIntakes: NutrientIntakesByType
)

typealias NutrientsByTypeFetchResponse = ApiResponseWithContent<NutrientsByTypeApiResponse>