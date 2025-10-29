package com.example.fitnessway.data.repository.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.network.nutrient.INutrientApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NutrientRepositoryImpl(
    private val apiService: INutrientApiService
) : INutrientRepository {
    override suspend fun getNutrientIntakes(date: String): Flow<UiState<NutrientsByType>> = flow {
        emit(UiState.Loading)
        val nutrientIntakesErrMsg = "Failed to get nutrient intakes"

        try {
            val response = apiService.getNutrientIntakes(date)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.data != null) {
                    emit(UiState.Success(body.data.nutrientIntakes))
                } else {
                    emit(UiState.Error(nutrientIntakesErrMsg))
                }
            } else {
                emit(UiState.Error(nutrientIntakesErrMsg))
            }

        } catch (_: Exception) {
            emit(UiState.Error(nutrientIntakesErrMsg))
        }
    }.flowOn(Dispatchers.IO)
}