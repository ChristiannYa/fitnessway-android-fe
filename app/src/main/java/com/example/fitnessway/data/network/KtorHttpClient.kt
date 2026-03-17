package com.example.fitnessway.data.network

import android.os.Build
import com.example.fitnessway.data.model.MApi
import com.example.fitnessway.data.model.MAuth
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UUIDSerializer
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.SerializersModule
import java.util.UUID
import io.ktor.client.HttpClient as KtorHttpClient

@OptIn(ExperimentalSerializationApi::class)
fun createKtorHttpClient(tokensStateHolder: ITokensStateHolder): KtorHttpClient {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        namingStrategy = JsonNamingStrategy.SnakeCase
        decodeEnumsCaseInsensitive = true
        explicitNulls = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
        }
    }

    return KtorHttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        install(Auth) {
            bearer {
                // Load the current tokens
                loadTokens {
                    val accessToken = tokensStateHolder.tokensState.value.accessToken
                    val refreshToken = tokensStateHolder.tokensState.value.refreshToken

                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else null
                }

                // Called on 401
                refreshTokens {
                    val refreshToken = tokensStateHolder.tokensState.value.refreshToken

                    if (refreshToken == null) {
                        tokensStateHolder.clearTokens()
                        return@refreshTokens null
                    }

                    try {
                        val response = client.post(ApiUrls.Auth.REFRESH_URL) {
                            contentType(ContentType.Application.Json)

                            setBody(
                                MAuth.Api.Req.RefreshTokenRequest(
                                    refreshToken,
                                    "${Build.MANUFACTURER} ${Build.MODEL}"
                                )
                            )

                            // Marks this as a refresh request so Ktor doesn't retry it on 401
                            markAsRefreshTokenRequest()
                        }

                        val body = json
                            .decodeFromString<MApi.Model.ApiResponseWithContent<MAuth.Api.Res.RefreshTokenApiResponse>>(
                                response.bodyAsText()
                            )

                        val newAccessToken = body.data?.accessToken

                        if (newAccessToken != null) {
                            tokensStateHolder.setAccessToken(newAccessToken)
                            BearerTokens(newAccessToken, refreshToken)
                        } else {
                            tokensStateHolder.clearTokens()
                            null
                        }
                    } catch (e: Exception) {
                        logcat("refresh token exception: ${e.message}")
                        tokensStateHolder.clearTokens()
                        null
                    }
                }
            }
        }
    }
}