package com.example.fitnessway.util

import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByType

object Food {
    fun subtractNutrientsFromIntakes(
        currentIntakes: NutrientIntakesByType,
        foodLog: FoodLogData
    ): NutrientIntakesByType {
        val nutrients = foodLog.food.nutrients
        val servings = foodLog.servings

        fun updateIntake(
            intake: NutrientIntake,
            nutrientId: Int
        ): NutrientIntake {
            val foodNutrient = (nutrients.basic + nutrients.vitamin + nutrients.mineral).find {
                it.nutrient.id == nutrientId
            }

            return if (foodNutrient != null) {
                val amountToSubtract = foodNutrient.amount * servings
                intake.copy(intake = (intake.intake - amountToSubtract))
            } else intake
        }

        return NutrientIntakesByType(
            basic = currentIntakes.basic.map {
                updateIntake(
                    intake = it, nutrientId = it.nutrient.id
                )
            }, vitamin = currentIntakes.vitamin.map {
                updateIntake(
                    intake = it, nutrientId = it.nutrient.id
                )
            }, mineral = currentIntakes.mineral.map {
                updateIntake(
                    intake = it, nutrientId = it.nutrient.id
                )
            }
        )
    }
}