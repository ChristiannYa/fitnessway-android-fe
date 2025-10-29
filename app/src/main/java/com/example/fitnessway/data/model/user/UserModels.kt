package com.example.fitnessway.data.model.user

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,

    @SerialName("is_premium")
    val isPremium: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class UserApiResponse(
    val user: User
)

typealias UserFetchResponse = ApiResponseWithContent<UserApiResponse>