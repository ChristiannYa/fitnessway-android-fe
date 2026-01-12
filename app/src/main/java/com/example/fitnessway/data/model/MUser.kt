package com.example.fitnessway.data.model

import com.example.fitnessway.data.model.MUser.Model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MUser {
    object Model {
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
    }

    object Api {
        object Res {
            @Serializable
            data class UserApiGetResponse(
                val user: User
            )
        }
    }
}