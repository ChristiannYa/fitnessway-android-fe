package com.example.fitnessway.data.model

import kotlinx.serialization.Serializable

object MApi {
    object Model {
        @Serializable
        data class ApiResponse(
            val success: Boolean,
            val message: String
        )

        @Serializable
        data class ApiResponseWithContent<T>(
            val success: Boolean,
            val message: String,
            val data: T? = null
        )
    }
}