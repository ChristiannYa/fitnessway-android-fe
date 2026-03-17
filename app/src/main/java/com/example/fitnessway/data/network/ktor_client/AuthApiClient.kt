package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MApi
import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import com.example.fitnessway.util.jsonBody
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient

class AuthApiClient(private val client: KtorHttpClient) {
    suspend fun register(req: MAuth.Api.Req.RegisterRequest): MAuth.Api.Res.RegisterApiResponse =
        client.post(ApiUrls.Auth.REGISTER_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun login(req: MAuth.Api.Req.LoginRequest): MAuth.Api.Res.LoginApiResponse =
        client.post(ApiUrls.Auth.LOGIN_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun logout(req: MAuth.Api.Req.LogoutRequest): MApi.Model.ApiResponse =
        client.post(ApiUrls.Auth.LOGOUT_URL) {
            jsonBody(req)
        }.body<MApi.Model.ApiResponse>()
}