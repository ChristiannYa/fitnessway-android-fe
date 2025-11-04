package com.example.fitnessway.util.form

object FormStates {
    data class FoodLog(
        val servings: String,
        val amountPerServing: String,
        val time: String
    )
}