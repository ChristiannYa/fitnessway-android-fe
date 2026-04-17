package com.example.fitnessway.util.extensions

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.OptimisticUpdate
import com.example.fitnessway.data.model.m_26.PaginationData
import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.logcat
import kotlin.math.ceil

fun PaginationData.calc(updateType: OptimisticUpdate, offset: Long): PaginationData {
    fun log(log: String) {
        logcat("[AppExtensions, PaginationData.calc] $log", Constants.LogLevel.VERBOSE)
    }

    log("offset: $offset")

    val newTotalCount = when (updateType) {
        OptimisticUpdate.REMOVE -> this.totalCount - 1
        OptimisticUpdate.ROLLBACK -> this.totalCount + 1
    }
    log("newTotalCount: $newTotalCount")

    val newPageCount = ceil(newTotalCount.toDouble() / Pagination.LIMIT.toDouble()).toInt()
    log("newPageCount: $newPageCount")

    val newCurrentPage = (offset.toInt() / Pagination.LIMIT) + 1
    log("newCurrentPage: $newCurrentPage")

    return PaginationData(
        totalCount = newTotalCount,
        pageCount = newPageCount,
        currentPage = newCurrentPage
    )
}