package com.example.fitnessway.util.form

object FormStates {
    data class FoodCreation(
        val name: String,
        val brand: String,
        val amountPerServing: String,
        val servingUnit: String,
        val nutrients: Map<Int, String> = emptyMap()
    )

    data class FoodLog(
        val servings: String,
        val amountPerServing: String,

        // Original value from the database
        val amountPerServingDb: Double,

        val time: String,
    )
}