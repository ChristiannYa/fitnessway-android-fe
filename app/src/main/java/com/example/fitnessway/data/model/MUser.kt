package com.example.fitnessway.data.model

import com.example.fitnessway.data.model.MUser.Model.User
import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    ADMIN,
    CONTRIBUTOR,
    USER
}

object MUser {
    object Model {
        @Serializable
        data class User(
            val id: String,
            val name: String,
            val email: String,
            val isPremium: Boolean,
            val createdAt: String,
            val updatedAt: String,
            val type: UserType
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