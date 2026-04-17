package com.example.fitnessway.data.model.m_26

import com.example.fitnessway.constants.Pagination
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
    
    fun getServerOffset() = ((this.currentPage - 1) * Pagination.LIMIT).toLong()
    fun getNextOffset() = (this.currentPage * Pagination.LIMIT).toLong()
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