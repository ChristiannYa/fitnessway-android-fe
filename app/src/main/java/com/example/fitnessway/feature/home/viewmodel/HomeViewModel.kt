package com.example.fitnessway.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.mappers.mapfl
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toNutrientPreview
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.model.m_26.EdibleLogAddRequest
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.NutrientIntakeMath
import com.example.fitnessway.data.model.m_26.PaginationResult
import com.example.fitnessway.data.repository.app_food.IAppFoodRepository
import com.example.fitnessway.data.repository.edible_list.food.IUserFoodRepository
import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.food.IFoodRecentLog
import com.example.fitnessway.data.repository.edible_recent_log.supplement.ISupplementRecentLog
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.user_supplement.IUserSupplementRepository
import com.example.fitnessway.data.state.IApplicationStateStore
import com.example.fitnessway.feature.home.manager.IHomeManager
import com.example.fitnessway.feature.home.manager.date.IDateManager
import com.example.fitnessway.feature.home.manager.foodlog.IFoodLogManager
import com.example.fitnessway.feature.home.manager.ui.IUiManager
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.date_time.IAppDateTimeFormatter
import com.example.fitnessway.util.extensions.calcDailyIntakes
import com.example.fitnessway.util.extensions.calcFoodLogNutrients
import com.example.fitnessway.util.extensions.toPrecisedString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appFoodRepo: IAppFoodRepository,
    private val nutrientRepo: INutrientRepository,
    private val userFoodRepo: IUserFoodRepository,
    private val userSupplementRepo: IUserSupplementRepository,
    private val foodLogRepo: IEdibleLogRepository,
    private val foodRecentLogRepo: IFoodRecentLog,
    private val supplementRecentLogRepo: ISupplementRecentLog,
    private val managers: IHomeManager,
    val appStateStore: IApplicationStateStore,
    val dateTimeFormatter: IAppDateTimeFormatter
) : ViewModel(),
    IFoodLogManager by managers.foodLog,
    IDateManager by managers.date,
    IUiManager by managers.ui {

    val userFlow: StateFlow<MUser.Model.User?> = appStateStore.userStateHolder.userState
        .map { it.user }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _appFoodSearchQuery = MutableStateFlow("")
    val appFoodSearchQuery: StateFlow<kotlin.String> = _appFoodSearchQuery

    val appFoodRepoUiState = appFoodRepo.uiState
    val nutrientRepoUiState = nutrientRepo.uiState
    val userFoodRepoUiState = userFoodRepo.uiState
    val userSupplementRepoUiState = userSupplementRepo.uiState
    val foodLogRepoUiState = foodLogRepo.uiState
    val foodRecentLogRepoUiState = foodRecentLogRepo.uiState
    val supplementRecentLogRepoUiState = supplementRecentLogRepo.uiState

    private fun getKebabDate() = dateTimeFormatter.formatKebabDate(managers.date.selectedDate.value)

    private var debounceJob: Job? = null

    fun getAppFoods(query: kotlin.String) {
        _appFoodSearchQuery.value = query

        debounceJob?.cancel()

        if (query.isBlank()) {
            appFoodRepo.clearAppFoods()
            return
        }

        appFoodRepo.clearAppFoods()

        debounceJob = viewModelScope.launch {
            delay(600)
            appFoodRepo.searchAppFoods(query)
        }
    }

    fun getAppFoodById(id: Int) = appFoodRepo.findAppFoodById(id)
    fun getMoreAppFoods(query: kotlin.String) = appFoodRepo.loadMoreAppFoods(query)

    fun getUserFoods() = userFoodRepo.load()
    fun getMoreUserFoods() = userFoodRepo.loadMore()

    fun getUserSupplements() = userSupplementRepo.load()
    fun getMoreUserSupplements() = userSupplementRepo.loadMore()

    fun getFoodLogs() = foodLogRepo.load(getKebabDate())
    fun refreshFoodLogs() = foodLogRepo.refresh(getKebabDate())

    fun getRecentlyLoggedFoods() = foodRecentLogRepo.load()
    fun getMoreRecentlyLoggedFoods() = foodRecentLogRepo.loadMore()

    fun getRecentlyLoggedSupplements() = supplementRecentLogRepo.load()
    fun getMoreRecentlyLoggedSupplements() = supplementRecentLogRepo.loadMore()

    fun getNutrientIntakes() = nutrientRepo.loadNutrientIntakes(getKebabDate())
    fun refreshNutrientIntakes() = nutrientRepo.refreshNutrientIntakes(getKebabDate())

    fun addFoodLog() {
        val foodLogFormState = managers.foodLog.foodLogFormState.value?.data ?: return
        val selectedFood = managers.foodLog.foodToLog.value ?: return
        val category = managers.foodLog.foodLogCategory.value ?: return
        val searchCriteria = managers.foodLog.searchCriteria.value ?: return

        val originalRecentlyLoggedListPager = when (searchCriteria.edibleType) {
            EdibleType.FOOD -> foodRecentLogRepo.uiState.value.uiStatePager.uiState
            EdibleType.SUPPLEMENT -> supplementRecentLogRepo.uiState.value.uiStatePager.uiState
        }
            .toSuccessOrNull()
            ?: return

        val loggedFoodPreview = FoodPreview(
            id = selectedFood.id,
            base = selectedFood.information.base,
            nutrientPreview = selectedFood.information.nutrients.toNutrientPreview(),
            source = searchCriteria.source
        )

        fun handleRecentListOptimisticUpdate(
            optimisticData: PaginationResult<FoodPreview>,
            edibleType: EdibleType
        ) {
            when (edibleType) {
                EdibleType.FOOD -> foodRecentLogRepo.updateState {
                    it.copy(uiStatePager = UiStatePager(UiState.Success(optimisticData)))
                }

                EdibleType.SUPPLEMENT -> supplementRecentLogRepo.updateState {
                    it.copy(uiStatePager = UiStatePager(UiState.Success(optimisticData)))
                }
            }
        }

        handleRecentListOptimisticUpdate(
            optimisticData = originalRecentlyLoggedListPager.copy(
                data = listOf(loggedFoodPreview) + (originalRecentlyLoggedListPager.data
                    .filter { it.id != loggedFoodPreview.id })
            ),
            edibleType = searchCriteria.edibleType
        )

        val kebabDate = getKebabDate()

        val request = EdibleLogAddRequest(
            edibleId = selectedFood.id,
            edibleType = searchCriteria.edibleType.name,
            servings = foodLogFormState.servings.toDouble(),
            category = category.name,
            time = "$kebabDate ${foodLogFormState.time}",
            source = searchCriteria.source.name
        )

        viewModelScope.launch {
            foodLogRepo.add(request, kebabDate).collect { state ->

                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogAddState = state) }
                        foodLogRepo.refresh(kebabDate)
                        nutrientRepo.refreshNutrientIntakes(kebabDate)
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodLogAddState = state) }

                        handleRecentListOptimisticUpdate(
                            optimisticData = originalRecentlyLoggedListPager,
                            edibleType = searchCriteria.edibleType
                        )
                    }

                    is UiState.Loading -> {
                        _uiState.update { it.copy(foodLogAddState = state) }
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateFoodLog() {
        // Check for states before proceeding
        val formState = managers.foodLog.foodLogEditionFormState.value ?: return
        val selectedFoodLog = managers.foodLog.selectedFoodLog.value ?: return

        val apiDate = getKebabDate()

        // Get original data to update optimistically
        val originalFoodLogsState = foodLogRepo.uiState.value.foodLogs[apiDate]
        val originalNutrientIntakesState = nutrientRepo.uiState.value.nutrientIntakesCache[apiDate]

        // Only proceed if there are food logs
        if (originalFoodLogsState !is UiState.Success ||
            originalNutrientIntakesState !is UiState.Success
        ) return

        val originalFoodLogs = originalFoodLogsState.data
        val originalNutrientIntakes = originalNutrientIntakesState.data

        // Gather new nutrient data based on amount per servings
        val newNutrientData = selectedFoodLog.foodInformation.nutrients
            .calcFoodLogNutrients(
                currentServings = selectedFoodLog.servings,
                newServings = formState.data.servings.toDouble()
            )

        // Update the current food log's nutrients
        val foodWithUpdatedNutrients = selectedFoodLog.foodInformation.copy(
            nutrients = newNutrientData
        )

        // Create the updated food log information
        val updatedFoodLog = FoodLog(
            id = selectedFoodLog.id,
            category = selectedFoodLog.category,
            time = selectedFoodLog.time,
            loggedAt = selectedFoodLog.loggedAt,
            servings = formState.data.servingsPrecised.toPrecisedString(4).toDouble(),
            userFoodSnapshotStatus = selectedFoodLog.userFoodSnapshotStatus,
            userFoodSnapshotId = selectedFoodLog.userFoodSnapshotId,
            source = selectedFoodLog.source,
            foodId = selectedFoodLog.foodId,
            foodInformation = foodWithUpdatedNutrients
        )

        // Change the selected food log's nutrients value
        managers.foodLog.setSelectedFoodLog(updatedFoodLog)

        // Create optimistic food logs
        val optimisticFoodLogs = originalFoodLogs.mapfl { category, logs ->
            logs
                .filter { it.id != selectedFoodLog.id }
                .let { if (category == updatedFoodLog.category) it + updatedFoodLog else it }
        }

        // Create optimistic nutrient intakes
        val intakesWithoutFoodLog = originalNutrientIntakes.calcDailyIntakes(
            nutrients = selectedFoodLog.foodInformation.nutrients.toList(),
            intakeMath = NutrientIntakeMath.SUBTRACT
        )

        val optimisticIntakes = intakesWithoutFoodLog.calcDailyIntakes(
            nutrients = updatedFoodLog.foodInformation.nutrients.toList(),
            intakeMath = NutrientIntakeMath.ADD
        )

        val request = FoodLogUpdateRequest(
            foodLogId = updatedFoodLog.id,
            userFoodSnapshotId = updatedFoodLog.userFoodSnapshotId,
            servings = updatedFoodLog.servings
        )

        // Update UI immediately
        foodLogRepo.updateState {
            it.copy(
                foodLogs = it.foodLogs + (apiDate to UiState.Success(
                    optimisticFoodLogs
                ))
            )
        }

        nutrientRepo.updateState {
            it.copy(
                nutrientIntakesCache = it.nutrientIntakesCache + (apiDate to UiState.Success(
                    optimisticIntakes
                ))
            )
        }

        // Send the api request
        viewModelScope.launch {
            foodLogRepo.update(request, apiDate).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogUpdateState = state) }
                    }

                    is UiState.Error -> {
                        // Provide feedback to UI
                        _uiState.update { it.copy(foodLogUpdateState = state) }

                        // Change updated food to the previously selected food log
                        managers.foodLog.setSelectedFoodLog(selectedFoodLog)

                        // Obtain updated UI states after optimistic update
                        val currentFoodLogsState = foodLogRepo.uiState.value
                            .foodLogs[apiDate]

                        val currentNutrientIntakesState = nutrientRepo.uiState.value
                            .nutrientIntakesCache[apiDate]

                        if (currentFoodLogsState is UiState.Success) {
                            val revertedFoodLogs = currentFoodLogsState.data.mapfl { category, logs ->
                                (logs.filter { it.id != updatedFoodLog.id }
                                    .let {
                                        if (category == selectedFoodLog.category) {
                                            it + selectedFoodLog
                                        } else it
                                    })
                                    .sortedByDescending { it.id }
                            }

                            foodLogRepo.updateState {
                                it.copy(
                                    foodLogs = it.foodLogs +
                                            (apiDate to UiState.Success(revertedFoodLogs))
                                )
                            }
                        }

                        if (currentNutrientIntakesState is UiState.Success) {

                            val intakesWithoutUpdatedFoodLog = currentNutrientIntakesState.data.calcDailyIntakes(
                                nutrients = updatedFoodLog.foodInformation.nutrients.toList(),
                                intakeMath = NutrientIntakeMath.SUBTRACT
                            )

                            val revertedIntakes = intakesWithoutUpdatedFoodLog.calcDailyIntakes(
                                nutrients = selectedFoodLog.foodInformation.nutrients.toList(),
                                intakeMath = NutrientIntakeMath.ADD
                            )

                            nutrientRepo.updateState {
                                it.copy(
                                    nutrientIntakesCache = it.nutrientIntakesCache +
                                            (apiDate to UiState.Success(revertedIntakes))
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private val _foodLogFailedDeletions = mutableMapOf<kotlin.String, MutableSet<FoodLog>>()

    fun deleteFoodLog() {
        val selectedFoodLogToRemove = managers.foodLog.selectedFoodLogToRemove.value ?: return
        val apiDate = getKebabDate()

        // Get current data to update optimistically
        val originalFoodLogsState = foodLogRepo.uiState.value.foodLogs[apiDate]
        val originalIntakesState = nutrientRepo.uiState.value.nutrientIntakesCache[apiDate]

        // Only proceed if we have data
        if (originalFoodLogsState !is UiState.Success ||
            originalIntakesState !is UiState.Success
        ) return

        // Extract current states' data
        val originalFoodLogs = originalFoodLogsState.data
        val originalIntakes = originalIntakesState.data

        // Get or create the failed deletions set for this date
        val failedDeletionsForDate = _foodLogFailedDeletions.getOrPut(apiDate) { mutableSetOf() }

        // Remove current food log id from failed deletions list
        failedDeletionsForDate.remove(selectedFoodLogToRemove)

        // Store optimistic food logs by filtering out the food log
        val optimisticFoodLogs = originalFoodLogs.mapfl { _, logs ->
            logs.filter { it.id != selectedFoodLogToRemove.id }
        }

        // Store optimistic intakes by removing the food log's intake amount from
        // the original intakes
        val optimisticIntakes = originalIntakes.calcDailyIntakes(
            nutrients = selectedFoodLogToRemove.foodInformation.nutrients.toList(),
            intakeMath = NutrientIntakeMath.SUBTRACT
        )

        // Update UI immediately
        foodLogRepo.updateState {
            it.copy(
                foodLogs = it.foodLogs
                        + (apiDate to UiState.Success(optimisticFoodLogs))
            )
        }

        nutrientRepo.updateState {
            it.copy(
                nutrientIntakesCache = it.nutrientIntakesCache
                        + (apiDate to UiState.Success(optimisticIntakes))
            )
        }

        viewModelScope.launch {
            foodLogRepo.delete(
                foodLogId = selectedFoodLogToRemove.id,
                date = apiDate
            ).collect { state ->

                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodLogDeleteState = state) }

                        // No need to remove the food log id form the failed deletion list because
                        // it's already removed in the beginning of the function
                    }

                    is UiState.Error -> {
                        // Provide feedback to UI
                        _uiState.update { it.copy(foodLogDeleteState = state) }

                        // Add failed deletion to failed deletions list
                        failedDeletionsForDate.add(selectedFoodLogToRemove)

                        // Obtain updated UI states after optimistic update
                        val currentFoodLogsState = foodLogRepo.uiState.value
                            .foodLogs[apiDate]

                        val currentIntakesState = nutrientRepo.uiState.value
                            .nutrientIntakesCache[apiDate]

                        // Update food logs data after failed deletion
                        if (currentFoodLogsState is UiState.Success) {
                            val currentFoodLogs = currentFoodLogsState.data

                            // Revert back to original food logs state
                            val revertedFoodLogs = currentFoodLogs
                                .mapfl { category, logs ->
                                    (logs + failedDeletionsForDate.filter {
                                        it.category == category
                                    })
                                        .distinctBy { it.id }
                                        .sortedByDescending { it.id }
                                }

                            foodLogRepo.updateState {
                                it.copy(
                                    foodLogs = it.foodLogs + (apiDate to UiState.Success(
                                        revertedFoodLogs
                                    ))
                                )
                            }
                        }

                        if (currentIntakesState is UiState.Success) {
                            val currentIntakes = currentIntakesState.data

                            // Revert back to original nutrient intakes state
                            val revertedNutrients = currentIntakes.calcDailyIntakes(
                                nutrients = selectedFoodLogToRemove.foodInformation.nutrients.toList(),
                                intakeMath = NutrientIntakeMath.ADD
                            )

                            nutrientRepo.updateState {
                                it.copy(
                                    nutrientIntakesCache = it.nutrientIntakesCache + (apiDate to UiState.Success(
                                        revertedNutrients
                                    ))
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun onResetFoodSelectionScreen() {
        viewModelScope.launch {
            delay(500)
            _appFoodSearchQuery.value = ""
            appFoodRepo.clearAppFoods()
            managers.foodLog.setFoodList(FoodLogListFilter.RECENTLY_LOGGED_FOODS)
        }
    }

    fun resetFoodLogAddState() {
        _uiState.update { it.copy(foodLogAddState = UiState.Idle) }
    }

    fun resetFoodLogUpdateState() {
        _uiState.update { it.copy(foodLogUpdateState = UiState.Idle) }
    }

    fun resetFoodLogDeleteState() {
        _uiState.update { it.copy(foodLogDeleteState = UiState.Idle) }
    }
}