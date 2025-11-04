package com.example.fitnessway.util.form

object FormStates {
    data class FoodLog(
        val servings: Double,
        val amountPerServing: Double,
        val time: String
    )
}