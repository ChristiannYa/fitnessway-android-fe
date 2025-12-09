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
data class NutrientPreferences(
    val goal: Double?,

    @SerialName("hex_color")
    val hexColor: String?
)

@Serializable
data class NutrientWithPreferences(
    val nutrient: Nutrient,
    val preferences: NutrientPreferences
)

@Serializable
data class NutrientAmountData(
    @SerialName("nutrient_with_preferences")
    val nutrientWithPreferences: NutrientWithPreferences,

    val amount: Double
)

@Serializable
data class NutrientsByTypeApiResponse(
    val nutrients: NutrientsByType<NutrientWithPreferences>
)

typealias NutrientsByTypeFetchResponse = ApiResponseWithContent<NutrientsByTypeApiResponse>

typealias NutrientIntakesByType = NutrientsByType<NutrientAmountData>

@Serializable
data class NutrientIntakesByTypeApiResponse(
    @SerialName("nutrient_intakes")
    val nutrientIntakes: NutrientIntakesByType
)

typealias NutrientIntakesByTypeFetchResponse = ApiResponseWithContent<NutrientIntakesByTypeApiResponse>

@Serializable
data class NutrientIdWithGoal(
    @SerialName("nutrient_id")
    val nutrientId: Int,

    val goal: Double
)

@Serializable
data class NutrientGoalsPostRequest(
    @SerialName("user_id")
    val userId: String,

    val goals: List<NutrientIdWithGoal>
)

@Serializable
data class NutrientGoalsApiPostResponse(
    @SerialName("upserted_goals")
    val upsertedGoals: List<NutrientIdWithGoal>
)

typealias NutrientGoalsPostResponse = ApiResponseWithContent<NutrientGoalsApiPostResponse>