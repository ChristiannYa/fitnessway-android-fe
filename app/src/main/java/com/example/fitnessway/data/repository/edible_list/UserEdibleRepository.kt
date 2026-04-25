package com.example.fitnessway.data.repository.edible_list

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PaginationResult
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserEdibleApiClient
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class UserEdibleRepository<T : RepositoryPagerState<UserEdible, T>>(
    private val edibleType: EdibleType,
    private val httpClient: HttpClient,
    private val apiClient: UserEdibleApiClient,
    private val repositoryScope: CoroutineScope
) : IEdibleRepository<T> {

    protected abstract val initialState: T

    private val _uiState by lazy { MutableStateFlow(initialState) }
    override val uiState: StateFlow<T> = _uiState

    private val edibleTypeString = edibleType.name.lowercase()

    private fun fetch(offset: Long = 0L): Flow<UiState<PaginationResult<UserEdible>>> =
        httpClient.makeRequest(
            apiCall = {
                apiClient.getList(
                    limit = Pagination.LIMIT,
                    offset = offset,
                    edibleType = edibleType.name
                )
            },
            extractData = { it.userEdiblesPagination },
            errMsg = "Failed to get your ${edibleTypeString}s",
            pathDescription = "User $edibleTypeString list"
        )

    override fun refresh() {
        _uiState.update { it.copyWithPager(pager = UiStatePager()) }

        repositoryScope.launch {
            fetch().collect { state ->
                _uiState.update { it.copyWithPager(pager = UiStatePager(state)) }
            }
        }
    }

    override fun load() {
        if (!_uiState.value.uiStatePager.uiState.hasState) refresh()
    }

    override fun loadMore() {
        _uiState.value.loadMore(_uiState, ::fetch, repositoryScope)
    }

    override suspend fun add(
        request: MFood.Api.Req.FoodAddRequest
    ): Flow<UiState<MFood.Model.FoodInformation>> =
        httpClient.makeRequest(
            apiCall = { apiClient.add(request) },
            extractData = { it.foodCreated },
            errMsg = "Failed to add $edibleTypeString",
            pathDescription = "add $edibleTypeString"
        )

    private var updateEdibleJob: Job? = null
    private val updateFoodFlow = MutableSharedFlow<UiState<MFood.Model.FoodInformation>>()
    override suspend fun update(
        request: MFood.Api.Req.FoodUpdateRequest
    ): Flow<UiState<MFood.Model.FoodInformation>> {
        updateEdibleJob?.cancel()

        updateEdibleJob = repositoryScope.launch {
            httpClient
                .makeRequest(
                    apiCall = { apiClient.update(request) },
                    extractData = { it.foodUpdated },
                    errMsg = "Failed to update $edibleTypeString",
                    pathDescription = "update $edibleTypeString"
                )
                .collect { updateFoodFlow.emit(it) }
        }

        return updateFoodFlow
    }

    override suspend fun delete(id: Int): Flow<UiState<MFood.Model.FoodInformation>> =
        httpClient.makeRequest(
            apiCall = { apiClient.delete(id) },
            extractData = { it.foodDeleted },
            errMsg = "Failed to delete $edibleTypeString",
            pathDescription = "delete $edibleTypeString"
        )

    override fun update(update: (T) -> T) {
        _uiState.update(update)
    }

    override fun clear() {
        _uiState.update { initialState }
    }
}