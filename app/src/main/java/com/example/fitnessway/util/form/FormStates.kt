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
        val amountPerServingDb: Double
    ) : FormStates

    data class FoodLog(
        val servings: String,
        val amountPerServing: String,
        val amountPerServingDb: Double, // Original value from the database
        val time: String,
    ) : FormStates
}