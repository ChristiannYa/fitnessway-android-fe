package com.example.fitnessway.data.model

import com.example.fitnessway.data.model.MUser.Model.User
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
            val createdAt: Instant,
            val updatedAt: String,
            val type: UserType,
            val timezone: String
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