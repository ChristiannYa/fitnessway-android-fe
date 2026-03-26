package com.example.fitnessway.data.mappers

import com.example.fitnessway.data.model.m_26.PaginationParams

fun PaginationParams.toRequestParams() = "limit=${this.limit}&offset=${this.offset}"