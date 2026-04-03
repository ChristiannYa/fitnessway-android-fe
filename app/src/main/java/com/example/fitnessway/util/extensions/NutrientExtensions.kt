package com.example.fitnessway.util.extensions

import com.example.fitnessway.data.mappers.mapnbt
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.data.model.m_26.NutrientIntakeMath
import com.example.fitnessway.data.model.m_26.NutrientIntakes
import com.example.fitnessway.data.model.m_26.NutrientsByType
import com.example.fitnessway.util.UNutrient

fun NutrientDataAmount.calcIntakes(): UNutrient.NutrientIntakeCalculation {
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

fun NutrientsByType<NutrientDataAmount>.calcFoodLogNutrients(
    currentServings: Double,
    newServings: Double
): NutrientsByType<NutrientDataAmount> = this.mapnbt { _, nutrients ->
    nutrients.map { nutrientInFood ->
        val originalAmount = if (currentServings != 0.0) {
            nutrientInFood.amount / currentServings
        } else nutrientInFood.amount

        nutrientInFood.copy(amount = originalAmount * newServings)
    }
}

fun NutrientIntakes.calcDailyIntakes(
    nutrients: List<NutrientDataAmount>,
    intakeMath: NutrientIntakeMath
): NutrientIntakes {
    val foodNutrientsMap = nutrients.associateBy { it.nutrientData.base.id }

    return this.mapnbt { _, intakes ->
        intakes.map { intake ->
            val nutrientDataAmount = foodNutrientsMap[intake.nutrientData.base.id]

            if (nutrientDataAmount != null) {
                val amount = when (intakeMath) {
                    NutrientIntakeMath.ADD -> intake.amount + nutrientDataAmount.amount
                    NutrientIntakeMath.SUBTRACT -> intake.amount - nutrientDataAmount.amount
                }

                intake.copy(amount = amount)
            } else intake
        }
    }
}