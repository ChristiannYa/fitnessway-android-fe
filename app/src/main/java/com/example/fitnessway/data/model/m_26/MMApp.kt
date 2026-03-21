package com.example.fitnessway.data.model.m_26

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResult<T>(
    val data: List<T>,
    val totalCount: Long,
    val pageCount: Int,
    val currentPage: Int
) {
    val hasMorePages: Boolean get() = currentPage < pageCount
}