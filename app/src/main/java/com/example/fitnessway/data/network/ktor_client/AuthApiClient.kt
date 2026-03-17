package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MApi
import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.data.network.ApiUrls
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.HttpClient as KtorHttpClient

class AuthApiClient(private val client: KtorHttpClient) {
    suspend fun register(req: MAuth.Api.Req.RegisterRequest): MAuth.Api.Res.RegisterApiResponse =
        client.post(ApiUrls.Auth.REGISTER_URL) {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()

    suspend fun login(req: MAuth.Api.Req.LoginRequest): MAuth.Api.Res.LoginApiResponse =
        client.post(ApiUrls.Auth.LOGIN_URL) {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()

    suspend fun logout(req: MAuth.Api.Req.LogoutRequest): MApi.Model.ApiResponse =
        client.post(ApiUrls.Auth.LOGOUT_URL) {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()
}