package com.example.fitnessway.util.extensions

import com.example.fitnessway.data.mappers.map
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.m_26.NutrientInFood
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.UNutrient

fun NutrientInFood.calcIntakes(): UNutrient.NutrientIntakeCalculation {
    val goal = this.nutrientData.preferences.goal ?: 0.0
    val progress = if (goal > 0) ((this.amount / goal) * 100) else 0.0
    val remaining = goal - this.amount
    val over = this.amount - goal

    return UNutrient.NutrientIntakeCalculation(
        intake = this.amount,
        progress = progress,
        remaining = remaining,
        over = over,
        isGoalMet = remaining == 0.0,
        isOverGoal = remaining < 0
    )
}

fun NutrientsByType<NutrientInFood>.calcIntakesFromServings(
    currentServings: Double,
    newServings: Double
): NutrientsByType<NutrientInFood> = this.map { _, nutrients ->
    nutrients.map { nutrientInFood ->
        val nutrientInFoodTarget = this.toList().find {
            it.nutrientData.base.id == nutrientInFood.nutrientData.base.id
        }

        if (nutrientInFoodTarget != null) {
            val originalAmount = nutrientInFoodTarget.amount / currentServings
            val newAmount = (originalAmount) * newServings

            nutrientInFood.copy(amount = newAmount)
        } else nutrientInFood
    }
}