package com.example.fitnessway.data.repository.user_supplement

import com.example.fitnessway.data.repository.IRepository

interface IUserSupplementRepository : IRepository<UserSupplementRepositoryUiState> {
    fun refresh()
    fun load()
    fun loadMore()
}