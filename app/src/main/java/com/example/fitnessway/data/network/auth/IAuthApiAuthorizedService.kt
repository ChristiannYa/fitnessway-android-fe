package com.example.fitnessway.data.network.auth

import com.example.fitnessway.data.model.MApi.Model.ApiResponse
import com.example.fitnessway.data.model.MAuth.Api.Req.LogoutRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiAuthorizedService {
    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<ApiResponse>
}