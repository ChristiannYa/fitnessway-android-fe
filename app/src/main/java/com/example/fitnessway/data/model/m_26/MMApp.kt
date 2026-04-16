package com.example.fitnessway.data.model.m_26

import kotlinx.serialization.Serializable

enum class OptimisticUpdate {
    REMOVE,
    ROLLBACK
}

@Serializable
data class PaginationResult<T>(
    val data: List<T>,
    val totalCount: Long,
    val pageCount: Int,
    val currentPage: Int
) {
    val hasMorePages: Boolean
        get() = currentPage < pageCount

    val offset: Long
        get() = this.data.size.toLong()
}

data class PaginationData(
    val totalCount: Long,
    val pageCount: Int,
    val currentPage: Int
)

data class PaginationParams(
    val limit: Int,
    val offset: Long
)