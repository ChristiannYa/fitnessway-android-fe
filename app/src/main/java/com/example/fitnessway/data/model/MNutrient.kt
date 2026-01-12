package com.example.fitnessway.data.model

import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithColor
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithGoal
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MNutrient {
    object Enum {
        @Serializable
        enum class NutrientType {
            @SerialName("basic")
            BASIC,

            @SerialName("vitamin")
            VITAMIN,

            @SerialName("mineral")
            MINERAL
        }
    }

    object Model {
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
        data class NutrientDataWithAmount(
            @SerialName("nutrient_with_preferences")
            val nutrientWithPreferences: NutrientWithPreferences,

            val amount: Double
        )
    }

    object Helpers {
        @Serializable
        data class NutrientIdWithAmount(
            @SerialName("nutrient_id")
            val nutrientId: Int,

            val amount: Double
        )

        @Serializable
        data class NutrientIdWithGoal(
            @SerialName("nutrient_id")
            val nutrientId: Int,

            val goal: Double
        )

        @Serializable
        data class NutrientIdWithColor(
            @SerialName("nutrient_id")
            val nutrientId: Int,

            @SerialName("hex_color")
            val hexColor: String
        )
    }

    object Api {
        object Req {
            @Serializable
            data class NutrientGoalsPostRequest(
                @SerialName("user_id")
                val userId: String,

                val goals: List<NutrientIdWithGoal>
            )

            @Serializable
            data class NutrientColorsPostRequest(
                @SerialName("user_id")
                val userId: String,

                val colors: List<NutrientIdWithColor>
            )
        }

        object Res {
            @Serializable
            data class NutrientsByTypeGetApiResponse(
                val nutrients: NutrientsByType<NutrientWithPreferences>
            )

            @Serializable
            data class NutrientIntakesByTypeGetApiResponse(
                @SerialName("nutrient_intakes")
                val nutrientIntakes: NutrientsByType<NutrientDataWithAmount>
            )

            @Serializable
            data class NutrientGoalsPostApiResponse(
                @SerialName("upserted_goals")
                val upsertedGoals: List<NutrientIdWithGoal>
            )

            @Serializable
            data class NutrientColorsApiPostResponse(
                @SerialName("updated_colors")
                val updatedColors: List<NutrientIdWithColor>
            )
        }
    }
}