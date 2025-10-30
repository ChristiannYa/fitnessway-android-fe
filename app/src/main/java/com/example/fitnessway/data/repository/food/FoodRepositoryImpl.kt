package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.network.food.IFoodApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FoodRepositoryImpl(
    private val apiService: IFoodApiService
) : IFoodRepository {
    override suspend fun getFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>> = flow{
        emit(UiState.Loading)
        val foodLogsErrMs = "Failed to get food logs"

        try {
            val response = apiService.getFoodLogs(date)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.data != null) {
                    emit(UiState.Success(body.data.foodLogs))
                } else {
                    emit(UiState.Error(foodLogsErrMs))
                }
            } else {
                emit(UiState.Error(foodLogsErrMs))
            }

        } catch (_: Exception) {
            emit(UiState.Error(foodLogsErrMs))
        }

    }.flowOn(Dispatchers.IO)
}