package com.example.fitnessway.data.network.auth

import com.example.fitnessway.data.model.MAuth.Api.Res.LoginApiResponse
import com.example.fitnessway.data.model.MAuth.Api.Req.LoginRequest
import com.example.fitnessway.data.model.api.ApiResponseWithContent
import com.example.fitnessway.data.model.MAuth.Api.Res.RegisterApiResponse
import com.example.fitnessway.data.model.MAuth.Api.Req.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiService {
   @POST("auth/login")
   suspend fun login(
      @Body request: LoginRequest
   ): Response<ApiResponseWithContent<LoginApiResponse>>

   @POST("auth/register")
   suspend fun register(
      @Body request: RegisterRequest
   ): Response<ApiResponseWithContent<RegisterApiResponse>>
}