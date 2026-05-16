package com.example.fitnessway.feature.profile.viewmodel.operation

import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.food.IFoodRecentLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.supplement.ISupplementRecentLogRepository
import com.example.fitnessway.data.repository.nutrient_intakes.INutrientIntakesRepository

class TimeZoneChangeOperation(
    edibleLogRepo: IEdibleLogRepository,
    foodRecentLogRepo: IFoodRecentLogRepository,
    supplementRecentLogRepo: ISupplementRecentLogRepository,
    nutrientIntakeRepo: INutrientIntakesRepository
) {
    private val repos = listOf(
        edibleLogRepo,
        foodRecentLogRepo,
        supplementRecentLogRepo,
        nutrientIntakeRepo
    )

    fun clearRepositories() = repos.forEach { it.clear() }
}