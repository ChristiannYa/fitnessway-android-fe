package com.example.fitnessway.data.model.food

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: change the server response to return a user's food in composition, so that
//  `UserFood` becomes simplified like so:
//  `data class UserFood(val userId: String, val food: Food)`

@Serializable
data class Food(
   val id: Int,
   val name: String,
   val brand: String?,

   @SerialName("amount_per_serving")
   val amountPerServing: Double,

   @SerialName("serving_unit")
   val servingUnit: String
)

@Serializable
data class UserFood(
   val id: Int,

   @SerialName("user_id")
   val userId: String,

   val name: String,
   val brand: String,

   @SerialName("amount_per_serving")
   val amountPerServing: Double,

   @SerialName("serving_unit")
   val servingUnit: String,
)

@Serializable
data class FoodsApiResponse(
   val foods: List<UserFood>
)

typealias FoodsApiFetchResponse = ApiResponseWithContent<FoodsApiResponse>

@Serializable
data class FoodNutrientAmountData(
   val nutrient: Nutrient,
   val amount: Double
)

@Serializable
data class FoodInformation(
   val information: Food,
   val nutrients: NutrientsByType<FoodNutrientAmountData>
)

@Serializable
data class FoodLogData(
   val id: Int,
   val category: String,
   val time: String,
   val servings: Double,

   @SerialName("food_status")
   val foodStatus: String,

   @SerialName("food_snapshot_id")
   val foodSnapshotId: Int,

   val food: FoodInformation
)

@Serializable
data class FoodLogsByCategory(
   val breakfast: List<FoodLogData>,
   val lunch: List<FoodLogData>,
   val dinner: List<FoodLogData>,
   val supplement: List<FoodLogData>,
)

@Serializable
data class FoodLogsApiResponse(
   @SerialName("food_logs")
   val foodLogs: FoodLogsByCategory
)

typealias FoodLogsApiFetchResponse = ApiResponseWithContent<FoodLogsApiResponse>