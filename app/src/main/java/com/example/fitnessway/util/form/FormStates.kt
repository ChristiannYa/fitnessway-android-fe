package com.example.fitnessway.util.form

sealed interface FormStates {
    data class FoodCreation(
        val name: String,
        val brand: String,
        val amountPerServing: String,
        val servingUnit: String,
        val nutrients: Map<Int, String> = emptyMap()
    ) : FormStates

    data class FoodEdition(
        val name: String,
        val brand: String,
        val amountPerServing: String,
        val servingUnit: String,
        val nutrients: Map<Int, String> = emptyMap()
    ) : FormStates

    data class FoodLogEdition(
        val servings: String,
        val amountPerServing: String,
        val foodAmountPerServing: Double,
        val servingsPrecised: Double,
    ) : FormStates

    data class FoodLog(
        val servings: String,
        val amountPerServing: String,
        val amountPerServingDb: Double,
        val time: String,
    ) : FormStates

    data class NutrientGoals(
        val goals: Map<Int, String> = emptyMap()
    ) : FormStates

    data class NutrientColors(
        val colors: Map<Int, String> = emptyMap()
    ) : FormStates
}