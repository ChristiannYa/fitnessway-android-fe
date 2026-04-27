package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MApi
import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient

class AuthApiClient(private val client: KtorHttpClient) {
    suspend fun register(req: MAuth.Api.Req.RegisterRequest): MAuth.Api.Res.RegisterApiResponse =
        client.post(ApiUrls.Auth.REGISTER_URL) {
            setJson(req)
        }.extractData()

    suspend fun login(req: MAuth.Api.Req.LoginRequest): MAuth.Api.Res.LoginApiResponse =
        client.post(ApiUrls.Auth.LOGIN_URL) {
            setJson(req)
        }.extractData()

    suspend fun logout(req: MAuth.Api.Req.LogoutRequest): MApi.Model.ApiResponse =
        client.post(ApiUrls.Auth.LOGOUT_URL) {
            setJson(req)
        }.body<MApi.Model.ApiResponse>()
}