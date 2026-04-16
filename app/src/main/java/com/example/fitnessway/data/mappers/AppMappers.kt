package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.m_26.PaginationData
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.model.m_26.PaginationResult

fun <T> PaginationResult<T>.toPaginationData() = PaginationData(
    totalCount = this.totalCount,
    pageCount = this.pageCount,
    currentPage = this.currentPage
)

fun <T> PaginationData.toResult(data: List<T>) = PaginationResult(
    data = data,
    totalCount = this.totalCount,
    pageCount = this.pageCount,
    currentPage = this.currentPage
)

fun PaginationParams.toRequestParams() = "limit=${this.limit}&offset=${this.offset}"