package com.example.fitnessway.data.model

import com.example.fitnessway.data.model.MFood.Enum.FoodLogFoodStatus
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfo
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithAmount
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MFood {
    object Enum {
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

        enum class FoodLogFoodStatus {
            @SerialName("present")
            PRESENT,

            @SerialName("updated")
            UPDATED,

            @SerialName("deleted")
            DELETED
        }

        enum class ListOption {
            Food,
            Supplement
        }
    }

    object Model {
        @Serializable
        data class FoodBaseInfo(
            val id: Int,
            val name: String,
            val brand: String?,

            @SerialName("amount_per_serving")
            val amountPerServing: Double,

            @SerialName("serving_unit")
            val servingUnit: String,
        )

        @Serializable
        data class FoodBaseInfoNullable(
            val id: Int,
            val name: String?,
            val brand: String?,

            @SerialName("amount_per_serving")
            val amountPerServing: Double?,

            @SerialName("serving_unit")
            val servingUnit: String?
        )

        @Serializable
        data class FoodInformation(
            val information: FoodBaseInfo,
            val nutrients: NutrientsByType<NutrientAmountData>
        )

        @Serializable
        data class FoodLogsByCategory(
            val breakfast: List<FoodLogData>,
            val lunch: List<FoodLogData>,
            val dinner: List<FoodLogData>,
            val supplement: List<FoodLogData>,
        )

        @Serializable
        data class FoodLogData(
            val id: Int,
            val category: String,
            val time: String,
            val servings: Double,

            @SerialName("food_status")
            val foodStatus: FoodLogFoodStatus,

            @SerialName("food_snapshot_id")
            val foodSnapshotId: Int?,

            val food: FoodInformation
        )
    }

    object Api {
        object Req {
            // ========== Food ==========
            @Serializable
            data class FoodAddRequest(
                @SerialName("user_id")
                val userId: String,

                val information: FoodBaseInfo,
                val nutrients: List<NutrientIdWithAmount>
            )

            @Serializable
            data class FoodUpdateRequest(
                @SerialName("user_id")
                val userId: String,

                val information: FoodBaseInfoNullable,

                @SerialName("upserted_nutrients")
                val upsertedNutrients: List<NutrientIdWithAmount>,

                @SerialName("deleted_nutrients")
                val deletedNutrients: List<Int>
            )

            // ========== Logs ==========
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
            data class FoodLogUpdateRequest(
                @SerialName("user_id")
                val userId: String,

                @SerialName("food_log_id")
                val foodLogId: Int,

                @SerialName("food_snapshot_id")
                val foodSnapshotId: Int?,

                val servings: Double
            )
        }

        object Res {
            // ========== Food ==========
            @Serializable
            data class FoodsGetApiResponse(
                // There will be cases where the user will not have any foods created, so the
                // list would be null
                @SerialName("foods")
                val foods: List<FoodInformation>?
            )

            @Serializable
            data class FoodAddApiResponse(
                @SerialName("food_created")
                val foodCreated: FoodInformation
            )

            @Serializable
            data class FoodUpdateApiResponse(
                @SerialName("updated_food")
                val updatedFood: FoodInformation
            )

            @Serializable
            data class FoodDeleteApiResponse(
                @SerialName("food_deleted")
                val foodDeleted: FoodInformation
            )

            // ========== Logs ==========
            @Serializable
            data class FoodLogsGetApiResponse(
                @SerialName("food_logs")
                val foodLogs: FoodLogsByCategory
            )

            @Serializable
            data class FoodLogAddApiResponse(
                @SerialName("food_log_added")
                val foodLogAdded: FoodLogData
            )

            @Serializable
            data class FoodLogUpdateApiResponse(
                @SerialName("updated_food_log")
                val updatedFoodLog: FoodLogData
            )

            @Serializable
            data class FoodLogDeleteApiResponse(
                @SerialName("food_log_deleted")
                val foodLogDeleted: FoodLogData
            )
        }
    }
}