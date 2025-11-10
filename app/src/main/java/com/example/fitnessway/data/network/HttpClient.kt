package com.example.fitnessway.data.network

import android.util.Log
import com.example.fitnessway.data.model.api.ApiResponseWithContent
import com.example.fitnessway.util.Constants
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

                if (body?.success == true && body.data != null) {
                    val extractedData = extractData(body.data)

                    // Invalidate cached URLs if provided
                    if (invalidatedUrls.isNotEmpty()) {
                        invalidatedUrls.forEach { url -> cacheManager.evictUrl(url) }
                    }

                    emit(UiState.Success(extractedData))
                } else {
                    val err = body?.message ?: errMsg

                    Log.d(Constants.DEBUG_TAG, err)
                    emit(UiState.Error(err))
                }
            } else {
                val errBody = response.errorBody()?.string()
                Log.d(Constants.DEBUG_TAG, "request error body: $errBody")

                emit(UiState.Error(errMsg))
            }

        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "request exception: $e")
            emit(UiState.Error(errMsg))
        }

    }.flowOn(Dispatchers.IO)
}