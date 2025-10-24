package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.auth.LoginApiPostResponse
import com.example.fitnessway.data.model.auth.LoginRequest
import com.example.fitnessway.data.model.auth.RegisterApiPostResponse
import com.example.fitnessway.data.model.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiService {
   @POST("auth/login")
   suspend fun login(
      @Body request: LoginRequest
   ): Response<LoginApiPostResponse>

   @POST("auth/register")
   suspend fun register(
      @Body request: RegisterRequest
   ): Response<RegisterApiPostResponse>
}