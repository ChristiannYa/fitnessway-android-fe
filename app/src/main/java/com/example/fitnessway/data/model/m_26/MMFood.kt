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

@Serializable
enum class PendingFoodStatus {
    PENDING,
    APPROVED,
    REJECTED;

    val isReviewed by lazy { this != PENDING }
}

@Serializable
data class FoodBase(
    val name: String,
    val brand: String? = null,
    val amountPerServing: Double,
    val servingUnit: ServingUnit
)

@Serializable
data class FoodInformation<N : NutrientEntry>(
    val base: FoodBase,
    val nutrients: List<N>
)

@Serializable
data class AppFood(
    val id: Int,
    val information: FoodInformation<NutrientInFood>,
    val createdBy: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant? = null
)

@Serializable
data class PendingFood(
    val id: Int,
    val information: FoodInformation<NutrientInFood>,
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
    val base: FoodBase
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