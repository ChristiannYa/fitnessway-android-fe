package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

object Http {
    fun <T, D> makeRequest(
        apiCall: suspend () -> Response<ApiResponseWithContent<T>>,
        extractData: (T) -> D,
        errMsg: String = "Error making request",
    ): Flow<UiState<D>> = flow {
        emit(UiState.Loading)

        try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.data != null) {
                    val extractedData = extractData(body.data)
                    emit(UiState.Success(extractedData))
                } else {
                    emit(UiState.Error(body?.message ?: errMsg))
                }
            } else {
                // val errBody = response.errorBody()?.string()

                emit(UiState.Error(errMsg))
            }

        } catch (_: Exception) {
            emit(UiState.Error(errMsg))
        }

    }.flowOn(Dispatchers.IO)
}