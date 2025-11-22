package com.example.fitnessway.data.model.food

import com.example.fitnessway.data.model.api.ApiResponseWithContent
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: change the server response to return a user's food (in a food log) in
//  composition, so that `UserFood` becomes simplified like so:
//  `data class UserFood(val userId: String, val food: Food)`

enum class FoodLogCategories {
    BREAKFAST,
    LUNCH,
    DINNER,
    SUPPLEMENT
}

enum class ServingUnits {
    G,
    MG,
    MCG,
    ML,
    OZ,
    KCAL;

    companion object {
        val units by lazy { entries.map { it.name.lowercase() } }
    }
}

enum class ListOption {
    Food,
    Supplement
}

interface IFoodInformation {
    val name: String
    val brand: String?
    val amountPerServing: Double
    val servingUnit: String
}

@Serializable
data class FoodInformationOptionals(
    val id: Int,
    val name: String?,
    val brand: String?,

    @SerialName("amount_per_serving")
    val amountPerServing: Double?,

    @SerialName("serving_unit")
    val servingUnit: String?,
)

@Serializable
data class Food(
    val id: Int,
    override val name: String,
    override val brand: String?,

    @SerialName("amount_per_serving")
    override val amountPerServing: Double,

    @SerialName("serving_unit")
    override val servingUnit: String
) : IFoodInformation

@Serializable
data class FoodNutrientAmountData(
    val nutrient: Nutrient,
    val amount: Double,
    val goal: Double?
)

@Serializable
data class FoodInformation(
    val information: Food,
    val nutrients: NutrientsByType<FoodNutrientAmountData>
)

@Serializable
data class FoodsApiResponse(
    // There will be cases where the user will not have any foods created, so the
    // list would be null
    @SerialName("foods")
    val foods: List<FoodInformation>?
)

typealias FoodsApiFetchResponse = ApiResponseWithContent<FoodsApiResponse>

@Serializable
data class FoodAddInfoApiFormat(
    override val name: String,
    override val brand: String?,

    @SerialName("amount_per_serving")
    override val amountPerServing: Double,

    @SerialName("serving_unit")
    override val servingUnit: String,
) : IFoodInformation

@Serializable
data class FoodAddNutrientAmountApiFormat(
    @SerialName("nutrient_id")
    val nutrientId: Int,

    val amount: Double
)

@Serializable
data class FoodAddRequest(
    @SerialName("user_id")
    val userId: String,

    val information: FoodAddInfoApiFormat,
    val nutrients: List<FoodAddNutrientAmountApiFormat>
)

@Serializable
data class FoodAddApiResponse(
    @SerialName("food_created")
    val foodCreated: FoodInformation
)

typealias FoodAddApiPostResponse = ApiResponseWithContent<FoodAddApiResponse>

@Serializable
data class FoodUpdateRequest(
    @SerialName("user_id")
    val userId: String,

    val information: FoodInformationOptionals,

    @SerialName("upserted_nutrients")
    val upsertedNutrients: List<FoodAddNutrientAmountApiFormat>,

    @SerialName("deleted_nutrients")
    val deletedNutrients: List<Int>
)

@Serializable
data class FoodUpdateApiResponse(
    @SerialName("updated_food")
    val updatedFood: FoodInformation
)

typealias FoodUpdateApiPutResponse = ApiResponseWithContent<FoodUpdateApiResponse>

@Serializable
data class FoodLogData(
    val id: Int,
    val category: String,
    val time: String,
    val servings: Double,

    @SerialName("food_status")
    val foodStatus: String,

    @SerialName("food_snapshot_id")
    val foodSnapshotId: Int?,

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

@Serializable
data class FoodLogAddRequest(
    @SerialName("user_id")
    val userId: String,

    @SerialName("food_id")
    val foodId: Int,

    val servings: Double,
    val category: String,
    val time: String
)

@Serializable
data class FoodLogAddApiResponse(
    @SerialName("food_log_added")
    val foodLogAdded: FoodLogData
)

typealias FoodLogAddPostResponse = ApiResponseWithContent<FoodLogAddApiResponse>

@Serializable
data class FoodLogDeleteApiResponse(
    @SerialName("food_log_deleted")
    val foodLogDeleted: FoodLogData
)

typealias FoodLogDeleteResponse = ApiResponseWithContent<FoodLogDeleteApiResponse>
