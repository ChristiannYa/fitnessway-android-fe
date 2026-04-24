@file:UseSerializers(UUIDSerializer::class)

package com.example.fitnessway.data.model.m_26

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
enum class EdibleType {
    FOOD,
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

enum class ListOption {
    PendingFood,
    Food,
    Supplement
}

enum class FoodLogListFilter {
    RECENTLY_LOGGED_FOODS,
    RECENTLY_LOGGED_SUPPLEMENTS,
    USER_FOODS,
    USER_SUPPLEMENTS
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
data class FoodPreview(
    val id: Int,
    val base: FoodBase,
    val nutrientPreview: NutrientPreview,
    val source: FoodSource
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
data class UserEdible(
    val id: Int,
    val information: FoodInformation,
    val lastLoggedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class RecentlyLoggedEdiblesResponse(
    val recentlyLoggedEdiblesPagination: PaginationResult<FoodPreview>
)

@Serializable
data class FoodLogAddRequest(
    val foodId: Int,
    val servings: Double,
    val category: FoodLogCategory,
    val time: String,
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
    val appFoodsPagination: PaginationResult<FoodPreview>
)

@Serializable
data class PendingFoodsGetResponse(
    val pendingFoodsPagination: PaginationResult<PendingFood>
)

@Serializable
data class UserEdiblesGetResponse(
    val userEdiblesPagination: PaginationResult<UserEdible>
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

data class FoodToLogSearchCriteria(
    val id: Int,
    val source: FoodSource,
    val edibleType: EdibleType
)