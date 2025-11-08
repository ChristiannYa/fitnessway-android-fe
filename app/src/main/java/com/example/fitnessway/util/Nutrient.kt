package com.example.fitnessway.util

import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User

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

        val progress = if (goal > 0) ((intake / goal) * 100).coerceIn(0.0, 100.0) else 0.0

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

    fun filterDisplayedNutrients(
        nutrientsByType: NutrientIntakesByType,
        nutrientType: NutrientType,
        user: User
    ): List<NutrientIntake> {
        val nutrients = when (nutrientType) {
            NutrientType.BASIC -> nutrientsByType.basic
            NutrientType.VITAMIN -> nutrientsByType.vitamin
            NutrientType.MINERAL -> nutrientsByType.mineral
        }

        return nutrients.filter { it.goal != null && (!it.nutrient.isPremium || user.isPremium) }
    }
}