package com.example.fitnessway.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MAuth {
    object Api {
        object Req {
            @Serializable
            data class RegisterRequest(
                val name: String,
                val email: String,
                val password: String,

                @SerialName("confirm_password")
                val confirmPassword: String,

                @SerialName("device_name")
                val deviceName: String
            )

            @Serializable
            data class LoginRequest(
                val email: String,
                val password: String,

                @SerialName("device_name")
                val deviceName: String
            )

            @Serializable
            data class LogoutRequest(
                @SerialName("refresh_token")
                val refreshToken: String,
            )

            @Serializable
            data class RefreshTokenRequest(
                @SerialName("refresh_token")
                val refreshToken: String,

                @SerialName("device_name")
                val deviceName: String
            )
        }

        object Res {
            @Serializable
            data class RegisterApiResponse(
                @SerialName("refresh_token")
                val refreshToken: String,

                @SerialName("access_token")
                val accessToken: String,
            )

            @Serializable
            data class LoginApiResponse(
                @SerialName("refresh_token")
                val refreshToken: String,

                @SerialName("access_token")
                val accessToken: String,
            )

            @Serializable
            data class RefreshTokenApiResponse(
                @SerialName("access_token")
                val accessToken: String,
            )
        }
    }
}