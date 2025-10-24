package com.example.fitnessway.data.model.auth

import com.example.fitnessway.data.model.api.ApiAuthResponse
import com.example.fitnessway.data.model.api.ApiResponse
import com.example.fitnessway.data.model.api.ApiResponseWithContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
   val email: String,
   val password: String,

   @SerialName("device_name")
   val deviceName: String
)

@Serializable
data class LoginApiResponse(
   @SerialName("refresh_token")
   val refreshToken: String,

   @SerialName("access_token")
   val accessToken: String,
)

typealias LoginApiPostResponse = ApiAuthResponse<LoginApiResponse>

@Serializable
data class LogoutRequest(
   @SerialName("refresh_token")
   val refreshToken: String,
)

typealias LogoutApiPostResponse = ApiResponse

@Serializable
data class RefreshTokenRequest(
   @SerialName("refresh_token")
   val refreshToken: String,

   @SerialName("device_name")
   val deviceName: String
)

@Serializable
data class RefreshTokenApiResponse(
   @SerialName("refresh_token")
   val refreshToken: String,

   @SerialName("access_token")
   val accessToken: String,
)

typealias RefreshTokenApiPostResponse = ApiResponseWithContent<RefreshTokenApiResponse>

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
data class RegisterApiResponse(
   @SerialName("refresh_token")
   val refreshToken: String,

   @SerialName("access_token")
   val accessToken: String,
)

typealias RegisterApiPostResponse = ApiResponseWithContent<RegisterApiResponse>