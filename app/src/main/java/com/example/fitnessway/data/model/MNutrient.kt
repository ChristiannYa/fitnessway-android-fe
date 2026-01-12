package com.example.fitnessway.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MNutrient {
    object Helpers {
        @Serializable
        data class NutrientIdWithAmount(
            @SerialName("nutrient_id")
            val nutrientId: Int,

            val amount: Double
        )
    }
}