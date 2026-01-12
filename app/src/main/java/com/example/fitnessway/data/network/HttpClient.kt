package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.MApi.Model.ApiResponseWithContent
import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class HttpClient(private val cacheManager: CacheManager) {
    fun <T, D> makeRequest(
        apiCall: suspend () -> Response<ApiResponseWithContent<T>>,
        extractData: (T) -> D,
        errMsg: String = "Error making request",
        invalidatedUrls: List<String> = emptyList()
    ): Flow<UiState<D>> = flow {
        emit(UiState.Loading)

        try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.data != null) {
                    val extractedData = extractData(body.data)

                    // Invalidate cached URLs if provided
                    if (invalidatedUrls.isNotEmpty()) {
                        invalidatedUrls.forEach { url -> cacheManager.evictUrl(url) }
                    }

                    emit(
                        UiState.Success(extractedData)
                    )
                } else {
                    val err = body?.message ?: errMsg

                    logcat(
                        message = err,
                        level = Constants.LogLevel.ERROR
                    )
                    emit(UiState.Error(err))
                }
            } else {
                val errBody = response.errorBody()?.string()
                logcat(
                    message = "request error body: $errBody",
                    level = Constants.LogLevel.ERROR
                )
                emit(UiState.Error(errMsg))
            }

        } catch (e: Exception) {
            logcat(
                message = "makeRequest exception: $e",
                level = Constants.LogLevel.ERROR
            )
            emit(UiState.Error(errMsg))
        }

    }.flowOn(Dispatchers.IO)
}