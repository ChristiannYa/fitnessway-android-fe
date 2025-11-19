package com.example.fitnessway.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType

object Nutrient {
    data class NutrientData(
        val intake: Double,
        val progress: Double,
        val remaining: Double,
        val over: Double,
        val isGoalMet: Boolean,
        val isOverGoal: Boolean
    )

    fun calcNutrientIntakeData(intakeData: NutrientIntake): NutrientData {
        val intake = intakeData.intake
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

    fun<T> filterNutrientsByType(
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
}