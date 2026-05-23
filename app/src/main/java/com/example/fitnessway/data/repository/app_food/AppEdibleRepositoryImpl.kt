package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.m_26.AppEdible
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.AppEdibleApiClient
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class AppEdibleWithBarcode(
    val edible: AppEdible,
    val barcode: String? = null
)

class AppEdibleRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: AppEdibleApiClient,
    private val repositoryScope: CoroutineScope
) : IAppEdibleRepository {
    private val _uiState = MutableStateFlow(AppEdibleRepositoryUiState())
    override val uiState: StateFlow<AppEdibleRepositoryUiState> = _uiState

    private fun fetchById(id: Int) =
        httpClient.makeRequest(
            apiCall = { apiClient.findAppFoodById(id) },
            extractData = { it.appEdible },
            errMsg = "Failed to fetch food by ID",
            pathDescription = "app food by id"
        )

    private fun fetchByBarcode(barcode: String) =
        httpClient.makeRequest(
            apiCall = { apiClient.findByBarcode(barcode) },
            extractData = { it.appEdible },
            errMsg = "Failed to fetch food by Barcode",
            pathDescription = "app food by barcode"
        )

    private val fetchedEdibleWithBarcodeList = mutableListOf<AppEdibleWithBarcode>()

    override fun findById(id: Int) {
        val edibleWithBarcode = fetchedEdibleWithBarcodeList.find { it.edible.id == id }
        edibleWithBarcode?.let { ewb ->
            _uiState.update { it.copy(appEdible = UiState.Success(ewb.edible)) }
            return
        }

        repositoryScope.launch {
            fetchById(id).collect { state ->
                _uiState.update { it.copy(appEdible = state) }

                if (state is UiState.Success) {
                    state.data?.let { fetchedEdibleWithBarcodeList.add(AppEdibleWithBarcode(it, null)) }
                }
            }
        }
    }

    override fun findByBarcode(barcode: String) {
        val edibleWithBarcode = fetchedEdibleWithBarcodeList.find { it.barcode == barcode }
        edibleWithBarcode?.let { ewb ->
            _uiState.update { it.copy(appEdible = UiState.Success(ewb.edible)) }
            return
        }

        repositoryScope.launch {
            fetchByBarcode(barcode).collect { state ->
                _uiState.update { it.copy(appEdible = state) }

                if (state is UiState.Success) {
                    state.data?.let { fetchedEdibleWithBarcodeList.add(AppEdibleWithBarcode(it, barcode)) }
                }
            }
        }
    }

    private fun fetchAppFoods(query: String, edibleType: EdibleType, offset: Long) =
        httpClient.makeRequest(
            apiCall = {
                apiClient.searchAppFoods(
                    query = query,
                    params = PaginationParams(Pagination.LIMIT, offset),
                    edibleType = edibleType.name
                )
            },
            extractData = { it.appFoodsPagination },
            errMsg = "Failed to fetch app foods",
            pathDescription = "app food list"
        )

    private var searchJob: Job? = null

    override fun search(query: String, edibleType: EdibleType) {
        searchJob?.cancel()

        _uiState.update {
            it.copy(
                appEdiblesUiStatePager = UiStatePager(),
            )
        }

        searchJob = repositoryScope.launch {
            fetchAppFoods(query, edibleType, 0).collect { state ->
                _uiState.update {
                    it.copy(
                        appEdiblesUiStatePager = UiStatePager(state)
                    )
                }
            }
        }
    }

    override fun loadMore(query: String, edibleType: EdibleType) {
        val pager = _uiState.value.appEdiblesUiStatePager
        if (pager.isLoadingMore) return

        val pagination = pager.toPaginationOrNull() ?: return
        if (!pagination.hasMorePages) return

        _uiState.update {
            it.copy(
                appEdiblesUiStatePager = pager.copy(isLoadingMore = true)
            )
        }

        repositoryScope.launch {
            fetchAppFoods(
                query = query,
                offset = pagination.currentPage * Pagination.LIMIT.toLong(),
                edibleType = edibleType
            ).collect { state ->
                when (state) {
                    is UiState.Success -> _uiState.update {
                        val current = it.appEdiblesUiStatePager.toPaginationOrNull()
                        val accumulated = (current?.data ?: emptyList()) + state.data.data

                        it.copy(
                            appEdiblesUiStatePager = UiStatePager(
                                uiState = UiState.Success(
                                    data = state.data.copy(data = accumulated),
                                ),
                                isLoadingMore = false
                            )
                        )
                    }

                    is UiState.Error -> _uiState.update {
                        it.copy(
                            appEdiblesUiStatePager = it.appEdiblesUiStatePager.copy(
                                isLoadingMore = false
                            )
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    override fun clear() {
        searchJob?.cancel()
        _uiState.update { it.copy(appEdiblesUiStatePager = UiStatePager(UiState.Idle)) }
    }
}