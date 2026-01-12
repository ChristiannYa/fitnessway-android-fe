package com.example.fitnessway.data.network.user

import com.example.fitnessway.data.model.MApi.Model.ApiResponseWithContent
import com.example.fitnessway.data.model.MUser.Api.Res.UserApiGetResponse
import retrofit2.Response
import retrofit2.http.GET

interface IUserApiService {
    @GET("user/me")
    suspend fun getUser(): Response<ApiResponseWithContent<UserApiGetResponse>>
}