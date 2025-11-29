package com.example.fitnessway.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter

object Nutrient {
    data class NutrientData(
        val intake: Double,
        val progress: Double,
        val remaining: Double,
        val over: Double,
        val isGoalMet: Boolean,
        val isOverGoal: Boolean
    )

    fun calcNutrientIntakeData(intakeData: NutrientAmountData): NutrientData {
        val intake = intakeData.amount
        val goal = intakeData.goal ?: 0.0

        val progress = if (goal > 0) ((intake / goal) * 100) else 0.0

        val remaining = goal - intake
        val over = intake - goal

        return NutrientData(
            intake = intake,
            progress = progress,
            remaining = remaining,
            over = over,
            isGoalMet = remaining == 0.0,
            isOverGoal = remaining < 0
        )
    }

    fun <T> getAllNutrients(
        nutrients: NutrientsByType<T>
    ): List<T> {
        return nutrients.basic + nutrients.vitamin + nutrients.mineral
    }

    fun <T> filterNutrientsByType(
        nutrients: NutrientsByType<T>,
        type: NutrientType
    ): List<T> {
        return when (type) {
            NutrientType.BASIC -> nutrients.basic
            NutrientType.VITAMIN -> nutrients.vitamin
            NutrientType.MINERAL -> nutrients.mineral
        }
    }

    fun List<NutrientApiFormat>.sortByPremiumStatus(
        isPremiumUser: Boolean
    ): List<NutrientApiFormat> {
        return if (!isPremiumUser) {
            this.sortedBy { it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun getNutrientColor(hexColor: String?): Color? {
        return hexColor?.let {
            Color(it.toColorInt())
        }
    }

    fun getFoodNutrientsAsMap(
        nutrients: NutrientsByType<NutrientAmountData>
    ): Map<Int, String> {
        return (nutrients.basic + nutrients.vitamin + nutrients.mineral)
            .associate { it.nutrient.id to doubleFormatter(it.amount) }
        // Result: {1="10.5", 2="20.3", 3="15"}
        //
        // If `.map` where to be used instead it would result in:
        // [(1, "10.5"), (2, "20.3"), (3, "15")]
        // which is a `List` but we need a map
    }

    data class IntakeComposables(
        val nutrients: List<NutrientAmountData>
    ) {

    }
}