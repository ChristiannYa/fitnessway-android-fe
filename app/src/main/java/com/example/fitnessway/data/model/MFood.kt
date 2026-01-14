package com.example.fitnessway.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SortByAlpha
import com.example.fitnessway.data.model.MFood.Enum.FoodLogFoodStatus
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfo
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.ui.shared.Structure
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

        enum class FoodSort(
            val icon: Structure.AppIconButtonSource
        ) {
            ALPHABETICALLY(Structure.AppIconButtonSource.Vector(Icons.Default.SortByAlpha)),
            CREATION_DATE(Structure.AppIconButtonSource.Vector(Icons.Default.CalendarToday)),
            FAVORITE(Structure.AppIconButtonSource.Vector(Icons.Default.Favorite)),
            RECENTLY_LOGGED(Structure.AppIconButtonSource.Vector(Icons.Default.History)),
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
        data class FoodMetaData(
            @SerialName("is_favorite")
            val isFavorite: Boolean,

            @SerialName("last_logged_at")
            val lastLoggedAt: String?,

            @SerialName("created_at")
            val createdAt: String,

            @SerialName("updated_at")
            val updatedAt: String
        )

        @Serializable
        data class FoodInformation(
            val information: FoodBaseInfo,
            val metadata: FoodMetaData,
            val nutrients: NutrientsByType<NutrientDataWithAmount>
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

            @SerialName("logged_at")
            val loggedAt: String,

            val servings: Double,

            @SerialName("food_status")
            val foodStatus: FoodLogFoodStatus,

            @SerialName("food_snapshot_id")
            val foodSnapshotId: Int?,

            val source: String,
            val food: FoodInformation
        )
    }

    object Api {
        object Req {
            @Serializable
            data class FoodAddRequest(
                val information: FoodBaseInfo,
                val nutrients: List<NutrientIdWithAmount>
            )

            @Serializable
            data class FoodUpdateRequest(
                val information: FoodBaseInfoNullable,

                @SerialName("upserted_nutrients")
                val upsertedNutrients: List<NutrientIdWithAmount>,

                @SerialName("deleted_nutrients")
                val deletedNutrients: List<Int>
            )

            @Serializable
            data class FoodSortUpdateRequest(
                @SerialName("food_sort")
                val foodSort: String
            )

            @Serializable
            data class FoodFavoriteStatusUpdateRequest(
                @SerialName("food_id")
                val foodId: Int,

                @SerialName("is_favorite")
                val isFavorite: Boolean
            )

            @Serializable
            data class FoodLogAddRequest(
                @SerialName("food_id")
                val foodId: Int,

                val servings: Double,
                val category: String,
                val source: String,
                val time: String
            )

            @Serializable
            data class FoodLogUpdateRequest(
                @SerialName("food_log_id")
                val foodLogId: Int,

                @SerialName("food_snapshot_id")
                val foodSnapshotId: Int?,

                val servings: Double
            )
        }

        object Res {
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
                @SerialName("food_updated")
                val foodUpdated: FoodInformation
            )

            @Serializable
            data class FoodDeleteApiResponse(
                @SerialName("food_deleted")
                val foodDeleted: FoodInformation
            )

            @Serializable
            data class FoodSortGetApiResponse(
                @SerialName("food_sort")
                val foodSort: String
            )

            @Serializable
            data class FoodSortUpdateApiResponse(
                @SerialName("food_sort")
                val foodSort: String
            )

            @Serializable
            data class FoodFavoriteStatusUpdateApiResponse(
                @SerialName("food_updated")
                val foodUpdated: FoodInformation
            )

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