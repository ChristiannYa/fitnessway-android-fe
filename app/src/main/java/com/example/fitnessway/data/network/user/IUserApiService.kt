package com.example.fitnessway.data.network.user

import com.example.fitnessway.data.model.user.UserFetchResponse
import retrofit2.Response
import retrofit2.http.GET

interface IUserApiService {
    @GET("user/me")
    suspend fun getUser(): Response<UserFetchResponse>
}