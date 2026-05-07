package com.example.fitnessway.data.repository.nutrient_intakes

import com.example.fitnessway.data.repository.IRepository

interface INutrientIntakesRepository : IRepository<NutrientIntakesRepositoryUiState> {
    fun refresh(date: String)
    fun load(date: String)
}