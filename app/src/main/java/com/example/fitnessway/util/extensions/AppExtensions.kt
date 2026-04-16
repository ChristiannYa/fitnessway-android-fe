package com.example.fitnessway.util.extensions

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.OptimisticUpdate
import com.example.fitnessway.data.model.m_26.PaginationData
import kotlin.math.ceil

fun PaginationData.calc(updateType: OptimisticUpdate, offset: Long): PaginationData {
    val newTotalCount = when (updateType) {
        OptimisticUpdate.REMOVE -> this.totalCount - 1
        OptimisticUpdate.ROLLBACK -> this.totalCount + 1
    }

    val newPageCount = ceil(newTotalCount.toDouble() / Pagination.LIMIT.toDouble()).toInt()
    val newCurrentPage = (offset.toInt() / Pagination.LIMIT) + 1

    return PaginationData(
        totalCount = newTotalCount,
        pageCount = newPageCount,
        currentPage = newCurrentPage
    )
}