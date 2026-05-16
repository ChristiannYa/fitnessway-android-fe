package com.example.fitnessway.data.repository.nutrient_intakes

import com.example.fitnessway.data.repository.IRepository

interface INutrientIntakesRepository : IRepository<NutrientIntakesRepositoryUiState> {
    fun setDate(date: String)

    fun load()
}