package com.example.fitnessway.data.repository.edible_list

import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.repository.IRepository
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IEdibleRepository<T> : IRepository<T> {
    fun refresh()
    fun load()
    fun loadMore()

    suspend fun add(request: EdibleAddRequest): Flow<UiState<Unit>>
    suspend fun update(request: FoodUpdateRequest): Flow<UiState<FoodInformation>>
    suspend fun delete(id: Int): Flow<UiState<FoodInformation>>
}