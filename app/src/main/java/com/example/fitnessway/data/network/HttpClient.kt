package com.example.fitnessway.data.network

import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HttpClient {
    fun <T, D> makeRequest(
        apiCall: suspend () -> T,
        extractData: (T) -> D,
        errMsg: String = "Error making request",
        pathDescription: String? = null,
    ): Flow<UiState<D>> = flow {
        emit(UiState.Loading)

        try {
            val data = apiCall()
            emit(UiState.Success(extractData(data)))

        } catch (e: Exception) {
            logcat(
                message = "makeRequest exception ($pathDescription): $e",
                level = Constants.LogLevel.ERROR
            )
            emit(UiState.Error(errMsg))
        }

    }.flowOn(Dispatchers.IO)
}