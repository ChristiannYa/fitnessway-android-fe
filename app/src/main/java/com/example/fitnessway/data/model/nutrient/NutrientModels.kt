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

    @SerialName("hex_color")
    val hexColor: String?,

    @SerialName("is_premium")
    val isPremium: Boolean
)

@Serializable
data class NutrientApiFormat(
    val nutrient: Nutrient,
    val goal: Double?
)

@Serializable
data class NutrientAmountData(
    val nutrient: Nutrient,
    val amount: Double,
    val goal: Double?
)

@Serializable
data class NutrientsByTypeApiResponse(
    val nutrients: NutrientsByType<NutrientApiFormat>
)

typealias NutrientsByTypeFetchResponse = ApiResponseWithContent<NutrientsByTypeApiResponse>

typealias NutrientIntakesByType = NutrientsByType<NutrientAmountData>

@Serializable
data class NutrientIntakesByTypeApiResponse(
    @SerialName("nutrient_intakes")
    val nutrientIntakes: NutrientIntakesByType
)

typealias NutrientIntakesByTypeFetchResponse = ApiResponseWithContent<NutrientIntakesByTypeApiResponse>