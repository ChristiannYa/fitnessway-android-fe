package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.auth.LoginApiPostResponse
import com.example.fitnessway.data.model.auth.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiService {
   @POST("auth/login")
   suspend fun login(
      @Body request: LoginRequest
   ) : Response<LoginApiPostResponse>

   /*
   @POST("auth/register")
   suspend fun register(
      @Body request: RegisterRequest
   ) : RegisterApiPostResponse

   @POST("auth/logout")
   suspend fun logout(
      @Body request: LogoutRequest
   ) : LogoutApiPostResponse

   @POST("auth/refresh")
   suspend fun refresh(
      @Body request: RefreshTokenRequest
   ) : RefreshTokenApiPostResponse
    */
}