package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.network.ApiUrls
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.HttpClient as KtorHttpClient

class UserApiClient(private val client: KtorHttpClient) {
    suspend fun getUser(): MUser.Api.Res.UserApiGetResponse =
        client.get(ApiUrls.User.USER_URL).body()
}