package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.auth.LogoutApiPostResponse
import com.example.fitnessway.data.model.auth.LogoutRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiAuthorizedService {
   @POST("auth/logout")
   suspend fun logout(
      @Body request: LogoutRequest
   ): Response<LogoutApiPostResponse>
}