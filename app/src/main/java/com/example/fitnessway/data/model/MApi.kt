package com.example.fitnessway.data.model

import kotlinx.serialization.Serializable

object MApi {
    object Model {
        @Serializable
        abstract class ApiResponse {
            abstract val success: Boolean
            abstract val message: String
        }

        @Serializable
        data class ApiResponseWithContent<T>(
            override val success: Boolean,
            override val message: String,
            val data: T?, // Can be null if the api does not return data
        ) : ApiResponse()
    }
}