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
        invalidatedUrls: List<String> = emptyList()
    ): Flow<UiState<D>> = flow {
        emit(UiState.Loading)

        try {
            val data = apiCall()
            val extractedData = extractData(data)

            if (invalidatedUrls.isNotEmpty()) {
                invalidatedUrls.forEach { logcat("invalidating url: $it") }
            }

            emit(UiState.Success(extractedData))

        } catch (e: Exception) {
            logcat(
                message = "makeRequest exception: ${e.message}",
                level = Constants.LogLevel.ERROR
            )
            emit(UiState.Error(errMsg))
        }

    }.flowOn(Dispatchers.IO)
}