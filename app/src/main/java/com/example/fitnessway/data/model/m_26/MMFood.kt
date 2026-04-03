@file:UseSerializers(UUIDSerializer::class)

package com.example.fitnessway.data.model.m_26

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import com.example.fitnessway.R
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID
import kotlin.time.Instant

@Serializable
enum class ServingUnit {
    G,
    MG,
    MCG,
    ML,
    OZ,
    KCAL
}

enum class FoodLogCategory {
    BREAKFAST,
    LUNCH,
    DINNER,
    SUPPLEMENT
}

@Serializable
enum class PendingFoodStatus {
    PENDING,
    APPROVED,
    REJECTED;

    val isReviewed by lazy { this != PENDING }
}

@Serializable
enum class UserFoodSnapshotStatus {
    PRESENT,
    UPDATED,
    DELETED
}

@Serializable
enum class FoodSource {
    APP,
    USER
}

enum class ListOption(
    val icon: Structure.AppIconButtonSource
) {
    PendingFood(Structure.AppIconButtonSource.Vector(Icons.Default.AccessTime)),
    Food(Structure.AppIconButtonSource.Resource(R.drawable.food)),
    Supplement(Structure.AppIconButtonSource.Resource(R.drawable.energy))
}

@Serializable
data class FoodLog(
    val id: Int,
    val category: FoodLogCategory,
    val time: Instant,
    val loggedAt: Instant,
    val servings: Double,
    val userFoodSnapshotStatus: UserFoodSnapshotStatus? = null,
    val userFoodSnapshotId: Int?,
    val source: FoodSource,
    val foodId: Int?,
    val foodInformation: FoodInformation
)

@Serializable
data class FoodLogsCategorized(
    val breakfast: List<FoodLog>,
    val lunch: List<FoodLog>,
    val dinner: List<FoodLog>,
    val supplement: List<FoodLog>
)

@Serializable
data class FoodBase(
    val name: String,
    val brand: String? = null,
    val amountPerServing: Double,
    val servingUnit: ServingUnit
)

@Serializable
data class FoodInformation(
    val base: FoodBase,
    val nutrients: NutrientsByType<NutrientDataAmount>
)

@Serializable
data class AppFood(
    val id: Int,
    val information: FoodInformation,
    val createdBy: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant? = null
)

@Serializable
data class PendingFood(
    val id: Int,
    val information: FoodInformation,
    val status: PendingFoodStatus,
    val createdBy: UUID?,
    val reviewedBy: UUID? = null,
    val reviewedAt: Instant? = null,
    val createdAt: Instant,
    val rejectionReason: String? = null,
)

@Serializable
data class FoodSearchResult(
    val id: Int,
    val base: FoodBase,
    val nutrientsPreview: NutrientPreview
)

@Serializable
data class FoodLogAddRequest(
    val foodId: Int,
    val servings: Double,
    val category: FoodLogCategory,
    val time: Instant,
    val source: FoodSource
)

@Serializable
data class FoodLogsResponse(
    val foodLogs: FoodLogsCategorized
)

@Serializable
data class FoodLogAddResponse(
    val foodLogAdded: FoodLog
)

@Serializable
data class FoodLogUpdateRequest(
    val foodLogId: Int,
    val userFoodSnapshotId: Int?,
    val servings: Double
)

@Serializable
data class FoodLogUpdateResponse(
    val foodLogUpdated: FoodLog
)

@Serializable
data class AppFoodFindByIdResponse(
    val appFood: AppFood?
)

@Serializable
data class AppFoodSearchResponse(
    val appFoodsPagination: PaginationResult<FoodSearchResult>
)

@Serializable
data class PendingFoodsGetResponse(
    val pendingFoodsPagination: PaginationResult<PendingFood>
)

@Serializable
data class PendingFoodAddRequest(
    val base: FoodBase,
    val nutrients: List<NutrientIdWithAmount>
)

@Serializable
data class PendingFoodAddResponse(
    val pendingFoodSubmitted: PendingFood
)

data class FoodInformationWithId(
    val id: Int,
    val information: FoodInformation
)

data class FoodPreview(
    val id: Int,
    val base: FoodBase,
    val nutrientPreview: NutrientPreview,
    val isUserPremium: Boolean
)

data class FoodToLogSearchCriteria(
    val id: Int,
    val source: FoodSource
)