package com.example.fitnessway.data.model.food

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Food(
   val id: Int,

   @SerialName("user_id")
   val userId: String,

   val name: String,
   val brand: String,

   @SerialName("amount_per_serving")
   val amountPerServing: Float,

   @SerialName("serving_unit")
   val servingUnit: String,
)

@Serializable
data class FoodsApiResponse(
   val foods: List<Food>
)

typealias FoodsApiFetchResponse = ApiResponseWithContent<FoodsApiResponse>