package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.model.m_26.UserTimezoneSetRequest
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.HttpClient as KtorHttpClient
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

class UserApiClient(private val client: KtorHttpClient) {

    suspend fun getUser(): MUser.Api.Res.UserApiGetResponse =
        client
            .get(ApiUrls.User.USER_URL)
            .extractData()

    suspend fun setTimezone(request: UserTimezoneSetRequest): KtorHttpResponse =
        client
            .patch(ApiUrls.User.USER_TIMEZONE_SET_URL) {
                setJson(request)
            }
}