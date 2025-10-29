package com.example.fitnessway.util

import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.user.User

data class NutrientData(
    val intake: Double,
    val progress: Double,
    val remaining: Double,
    val over: Double,
    val isGoalMet: Boolean,
    val isOverGoal: Boolean
)

fun calcNutrientData(n: NutrientIntake): NutrientData {
    val intake = n.intake
    val goal = n.goal ?: 0.0

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
    nutrients: List<NutrientIntake>,
    user: User
): List<NutrientIntake> {
    return nutrients.filter {
        it.goal != null && (!it.nutrient.isPremium || user.isPremium)
    }
}