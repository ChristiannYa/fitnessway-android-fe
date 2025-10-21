package com.example.fitnessway.data.model.api

import kotlinx.serialization.Serializable

// An abstract property means child classes MUST implement them
// "abstract" is needed for serialization to know that these properties
// will not be repeated in the child classes
@Serializable
abstract class ApiResponse{
   abstract val success: Boolean
   abstract val message: String
}

@Serializable
data class ApiResponseWithContent<T>(
   // By using override we make sure to not create new properties
   override val success: Boolean,
   override val message: String,
   val content: T?, // Can be null if the api does not return content
) : ApiResponse()