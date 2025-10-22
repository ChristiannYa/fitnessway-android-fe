package com.example.fitnessway.data.model.api

import kotlinx.serialization.Serializable

// @NOTE
// An abstract property means child classes MUST implement them
// "abstract" is needed for serialization to know that these properties
// will not be repeated in the child classes

// @NOTE
// Kotlinx Serialization treats nullable fields (String?) as required unless
// they are given a default value.

@Serializable
abstract class ApiResponse{
   abstract val success: Boolean
   abstract val message: String
}

@Serializable
data class ApiResponseWithContent<T>(
   override val success: Boolean,
   override val message: String,
   val data: T?, // Can be null if the api does not return data
) : ApiResponse()


// These are targeted types to authorization requests

@Serializable
data class ApiAuthResponse<T>(
   val success: Boolean,
   val message: String? = null,
   val data: T? = null,
   val errors: Map<String, String>? = null
)