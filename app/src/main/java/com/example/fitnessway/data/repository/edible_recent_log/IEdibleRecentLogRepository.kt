package com.example.fitnessway.data.repository.edible_recent_log

import com.example.fitnessway.data.repository.IRepository

interface IEdibleRecentLogRepository<T> : IRepository<T> {
    fun refresh()
    fun load()
    fun loadMore()
}