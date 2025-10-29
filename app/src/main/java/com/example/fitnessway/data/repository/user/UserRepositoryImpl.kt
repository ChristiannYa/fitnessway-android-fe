package com.example.fitnessway.data.repository.user

import android.util.Log
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.data.network.user.IUserApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepositoryImpl(
    private val apiService: IUserApiService,
) : IUserRepository {

    override suspend fun getUser(): Flow<UiState<User>> = flow {
        emit(UiState.Loading)
        val userFetchErrMsg = "Failed to get user data"

        try {
            val response = apiService.getUser()

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.data != null) {
                    emit(UiState.Success(body.data.user))
                } else {
                    emit(UiState.Error(userFetchErrMsg))
                }
            } else {
                emit(UiState.Error(userFetchErrMsg))
            }

        } catch (e: Exception) {
            Log.d("fitnessway", "UserRepositoryImpl e: $e")
            emit(UiState.Error(userFetchErrMsg))
        }
    }.flowOn(Dispatchers.IO)
}
