package com.example.fitnessway.util.food.creation

fun getFormTitle(
    isSubmissionSuccessful: Boolean,
    currentStep: Int
): Pair<String, String> {
    val finalTitle = if (isSubmissionSuccessful) {
        "Go Back"
    } else "Minerals"

    return when (currentStep) {
        1 -> "Food Information" to "Add Nutrients"
        2 -> "Nutrients" to "Add Vitamins"
        3 -> "Vitamins" to "Add Minerals"
        4 -> finalTitle to "Create Food"
        else -> "" to ""
    }
}