package com.example.fitnessway.data.repository

import com.example.fitnessway.data.repository.edible_list.food.IUserFoodRepository
import com.example.fitnessway.data.repository.edible_list.supplement.IUserSupplementRepository
import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.food.IFoodRecentLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.supplement.ISupplementRecentLogRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient_intakes.INutrientIntakesRepository
import com.example.fitnessway.data.repository.pending.food.IPendingFoodRepository
import com.example.fitnessway.data.repository.pending.supplement.IPendingSupplementRepository
import com.example.fitnessway.data.repository.user.IUserRepository

class RepositoryOperations(
    userRepo: IUserRepository,
    userFoodRepo: IUserFoodRepository,
    userSupplementRepo: IUserSupplementRepository,
    edibleLogRepo: IEdibleLogRepository,
    foodRecentLogRepo: IFoodRecentLogRepository,
    supplementRecentLogRepo: ISupplementRecentLogRepository,
    nutrientRepo: INutrientRepository,
    nutrientIntakesRepo: INutrientIntakesRepository,
    pendingFoodRepo: IPendingFoodRepository,
    pendingSupplementRepository: IPendingSupplementRepository
) {
    private val all = listOf(
        userRepo,
        userFoodRepo,
        userSupplementRepo,
        edibleLogRepo,
        foodRecentLogRepo,
        supplementRecentLogRepo,
        nutrientRepo,
        nutrientIntakesRepo,
        pendingFoodRepo,
        pendingSupplementRepository
    )

    private val timezoneChange = listOf(
        edibleLogRepo,
        foodRecentLogRepo,
        supplementRecentLogRepo,
        nutrientIntakesRepo,
        pendingFoodRepo,
        pendingSupplementRepository
    )

    fun onTimezoneChange() = timezoneChange.forEach { it.clear() }

    fun onLogout() = all.forEach { it.clear() }
}