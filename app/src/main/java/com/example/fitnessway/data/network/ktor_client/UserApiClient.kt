package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import io.ktor.client.request.get
import io.ktor.client.HttpClient as KtorHttpClient

class UserApiClient(private val client: KtorHttpClient) {
    suspend fun getUser(): MUser.Api.Res.UserApiGetResponse =
        client.get(ApiUrls.User.USER_URL).extractApiData()
}