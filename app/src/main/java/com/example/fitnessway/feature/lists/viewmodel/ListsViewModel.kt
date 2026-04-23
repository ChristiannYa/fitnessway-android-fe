package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toM25NutrientDataWithAmount
import com.example.fitnessway.data.mappers.toM26NutrientInFood
import com.example.fitnessway.data.mappers.toNutrientIdAmountList
import com.example.fitnessway.data.mappers.toPaginationData
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.mappers.toPendingRequest
import com.example.fitnessway.data.mappers.toResult
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toType
import com.example.fitnessway.data.mappers.toUserFoodRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.data.model.m_26.OptimisticUpdate
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.repository.food_log.IFoodLogRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.pending_food.IPendingFoodRepository
import com.example.fitnessway.data.repository.user_food.IUserFoodRepository
import com.example.fitnessway.data.repository.user_supplement.IUserSupplementRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.creation.ICreationManager
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.request.IFoodRequestManager
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.extensions.calc
import com.example.fitnessway.util.toEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val pendingFoodRepo: IPendingFoodRepository,
    private val userFoodRepo: IUserFoodRepository,
    private val userSupplementRepo: IUserSupplementRepository,
    private val foodLogRepo: IFoodLogRepository,
    private val nutrientRepo: INutrientRepository,
    private val managers: IListsManagers,
    userStateHolder: IUserStateHolder
) : ViewModel() {

    init {
        managers.edition.init(viewModelScope)
    }

    val editionManager: IEditionManager get() = managers.edition
    val creationManager: ICreationManager get() = managers.creation
    val requestManager: IFoodRequestManager get() = managers.request

    private val _uiState = MutableStateFlow(ListsScreenUiState())
    val uiState: StateFlow<ListsScreenUiState> = _uiState.asStateFlow()

    private val _selectedList = MutableStateFlow<ListOption>(ListOption.Food)
    val selectedList: StateFlow<ListOption> = _selectedList

    val pendingFoodRepoUiState = pendingFoodRepo.uiState
    val userFoodRepoUiState = userFoodRepo.uiState
    val userSupplementRepoUiState = userSupplementRepo.uiState
    val nutrientRepoUiState = nutrientRepo.uiState

    val userFlow: StateFlow<MUser.Model.User?> = userStateHolder.userState
        .map { it.user }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun getNutrients() = nutrientRepo.loadNutrients()

    fun getFoods() = userFoodRepo.loadFoods()
    fun getMoreFoods() = userFoodRepo.loadMoreFoods()

    fun getSupplements() = userSupplementRepo.load()
    fun getMoreSupplements() = userSupplementRepo.loadMore()

    fun getPendingFoods() = pendingFoodRepo.loadPendingFoods()
    fun getMorePendingFoods() = pendingFoodRepo.loadMorePendingFoods()

    fun addFoodRequest() {
        val formState = managers.request.formState.value

        val nutrientDvMap = managers.request.nutrientDvControls.nutrientDvMap.value
        val nutrients = formState.nutrients.toNutrientIdAmountList(nutrientDvMap)
        val request = formState.toPendingRequest(nutrients)

        viewModelScope.launch {
            pendingFoodRepo.addPendingFood(request).collect { state ->
                _uiState.update { it.copy(foodRequestState = state) }
            }
        }
    }

    fun addFood() {
        val formState = managers.creation.formState.value

        val nutrientDvMap = managers.creation.nutrientDvControls.nutrientDvMap.value
        val nutrients = formState.nutrients.toNutrientIdAmountList(nutrientDvMap)
        val request = formState.toUserFoodRequest(nutrients)

        viewModelScope.launch {
            userFoodRepo.addFood(request).collect { state ->
                _uiState.update { it.copy(foodAddState = state) }
            }
        }
    }

    private var _originalFoodBeforeUpdate: UserEdible? = null

    fun updateFood() {
        val formState = managers.edition.foodEditionFormState.value ?: return
        val nutrientDvMap = managers.creation.nutrientDvControls.nutrientDvMap.value
        val selectedFoodId = managers.edition.selectedFood.value?.id ?: return

        // Get current data to update optimistically
        val originalFoodsPager = userFoodRepo.uiState.value.uiStatePager.uiState
            .toSuccessOrNull()
            ?: return

        // Obtain nutrient data
        val originalNutrients = nutrientRepoUiState.value.nutrientsUiState
            .toSuccessOrNull()
            ?: return

        // Obtain most recent version of the food from the repository
        val latestFood = originalFoodsPager.data
            .find { it.id == selectedFoodId }
            ?: return

        // Store original food if first update
        if (_originalFoodBeforeUpdate == null) _originalFoodBeforeUpdate = latestFood

        // Gather updated nutrient data
        val addedNutrients = managers.edition.addedNutrients.value
        val deletedNutrients = managers.edition.deletedNutrients.value
        val upsertedNutrients = formState.data.nutrients.toNutrientIdAmountList(nutrientDvMap)

        // Obtain added nutrients if they are present
        val addedNutrientsWithPreferences = addedNutrients.mapNotNull { addedNutrient ->
            originalNutrients.combine().find { it.nutrient.id == addedNutrient.id }
        }

        // Create a list of all original nutrients and added nutrients (if any)'s preferences metadata
        val allNutrientsWithPreferences = (latestFood.information.nutrients
            .toList()
            .map { it.toM25NutrientDataWithAmount().nutrientWithPreferences } + addedNutrientsWithPreferences)
            .associateBy { it.nutrient.id }

        // Create updated nutrient data
        val updatedFoodNutrientData = upsertedNutrients.mapNotNull { upsertedNutrient ->
            allNutrientsWithPreferences[upsertedNutrient.nutrientId]?.let {
                NutrientDataWithAmount(
                    nutrientWithPreferences = it,
                    amount = upsertedNutrient.amount
                )
            }
        }

        // Filter updated nutrients by type
        val updatedNutrientsByType = updatedFoodNutrientData
            .map { it.toM26NutrientInFood() }
            .toType()

        // Create the new food
        val optimisticFood = UserEdible(
            id = latestFood.id,
            information = com.example.fitnessway.data.model.m_26.FoodInformation(
                base = FoodBase(
                    formState.data.name,
                    brand = formState.data.brand,
                    amountPerServing = formState.data.amountPerServing.toDouble(),
                    servingUnit = formState.data.servingUnit.toEnum()
                ),
                nutrients = updatedNutrientsByType
            ),
            lastLoggedAt = latestFood.lastLoggedAt,
            createdAt = latestFood.createdAt,
            updatedAt = latestFood.updatedAt
        )

        // Create optimistic data
        val optimisticFoods = originalFoodsPager.data.map {
            if (it.id == latestFood.id) optimisticFood else it
        }

        // Update UI immediately
        managers.edition.setSelectedFood(optimisticFood)
        managers.edition.initializeFoodForm(optimisticFood)
        resetFoodEditionStates()

        _uiState.update { it.copy(foodUpdateState = UiState.Success(Unit)) }

        userFoodRepo.updateState {
            it.copy(
                uiStatePager = UiStatePager(
                    uiState = UiState.Success(
                        originalFoodsPager.copy(
                            data = optimisticFoods
                        )
                    ),
                    isLoadingMore = false
                )
            )
        }

        removeRecentlyLoggedFood(latestFood.id)

        // Create request
        val request = FoodUpdateRequest(
            information = FoodBaseInfoNullable(
                id = latestFood.id,
                name = formState.data.name,
                brand = formState.data.brand,
                amountPerServing = formState.data.amountPerServing.toDoubleOrNull(),
                servingUnit = formState.data.servingUnit
            ),
            upsertedNutrients = upsertedNutrients,
            deletedNutrients = deletedNutrients
        )

        // Send the api request
        viewModelScope.launch {
            userFoodRepo.updateFood(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodUpdateState = UiState.Success(Unit)) }
                        foodLogRepo.clearFoodLogsUiCache()

                        // Clear the original food's data
                        _originalFoodBeforeUpdate = null
                    }

                    is UiState.Error -> {
                        // Provide ui the error state
                        _uiState.update { it.copy(foodUpdateState = state) }

                        val revertedFood = _originalFoodBeforeUpdate

                        if (revertedFood != null) {
                            val currentFoodsPager = userFoodRepo.uiState.value.uiStatePager
                                .toPaginationOrNull()
                                ?: return@collect

                            val currentFoods = currentFoodsPager.data

                            val revertedFoods = currentFoods.map {
                                if (it.id == revertedFood.id) revertedFood else it
                            }

                            userFoodRepo.updateState {
                                it.copy(
                                    uiStatePager = UiStatePager(
                                        uiState = UiState.Success(
                                            currentFoodsPager.copy(
                                                data = revertedFoods
                                            )
                                        )
                                    )
                                )
                            }

                            managers.edition.initializeFoodForm(revertedFood)
                            managers.edition.setSelectedFood(revertedFood)
                        }

                        if (foodLogRepo.uiState.value.recentlyLogged.uiState.hasState) {
                            foodLogRepo.refreshRecentlyLogged()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private val _foodFailedDeletions = mutableSetOf<Pair<Int, UserEdible>>()
    private var _foodsBeforeDeletion: List<UserEdible> = emptyList()

    fun deleteFood() {
        val foodToRemove = managers.edition.selectedFood.value ?: return

        val originalPager = userFoodRepo.uiState.value.uiStatePager
            .toPaginationOrNull()
            ?: return

        // Store current foods (not already in list) before a successful dismissal
        _foodsBeforeDeletion = _foodsBeforeDeletion + run {
            originalPager.data.filter {
                it.id !in _foodsBeforeDeletion.map { f -> f.id }
            }
        }

        // Find the current food from the current list
        val current = originalPager.data
            .find { it.id == foodToRemove.id }
            ?: return

        // Store the original index/position of the food removed
        val originalIndex = _foodsBeforeDeletion
            .indexOfFirst { it.id == foodToRemove.id }
            .takeIf { it != -1 }
            ?: return

        // Remove the food removed from the failed deletion list.
        // This resets a previously failed dismissal so that the user can try again
        _foodFailedDeletions.removeIf { it.second.id == foodToRemove.id }

        // Update states optimistically
        _uiState.update { it.copy(foodDeleteState = UiState.Success(Unit)) }

        userFoodRepo.updateState {
            it.copy(
                uiStatePager = UiStatePager(
                    uiState = UiState.Success(
                        // Optimistic pagination when REMOVING the food
                        originalPager
                            .toPaginationData()
                            .calc(OptimisticUpdate.REMOVE, originalPager.getServerOffset())
                            .toResult(originalPager.data.filter { f -> f.id != foodToRemove.id })
                    ),
                    isLoadingMore = false
                )
            )
        }

        viewModelScope.launch {
            userFoodRepo.deleteFood(foodToRemove.id).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodDeleteState = UiState.Success(Unit)) }

                        // Reset foods before successful deletion if all deletions succeeded
                        if (_foodFailedDeletions.isEmpty()) {
                            _foodsBeforeDeletion = emptyList()
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodDeleteState = state) }

                        _foodFailedDeletions.add(originalIndex to current)

                        val currentPager = userFoodRepo.uiState.value.uiStatePager.uiState
                            .toSuccessOrNull()
                            ?: return@collect

                        val revertedPagination = currentPager
                            .toPaginationData()
                            .calc(OptimisticUpdate.ROLLBACK, currentPager.getServerOffset())
                            .toResult(
                                // Combine the current foods (modified after optimistic update)
                                // with the foods in the queue to rollback
                                (currentPager.data + _foodFailedDeletions.map { it.second })
                                    .distinctBy { it.id }
                                    .sortedBy { food ->
                                        // Original position from failed deletion being rolled back
                                        _foodFailedDeletions
                                            .find { it.second.id == food.id }
                                            ?.first
                                            ?: run {
                                                // Original position from the current list of a non-dismissed
                                                // food
                                                _foodsBeforeDeletion
                                                    .indexOfFirst { it.id == food.id }
                                                    .takeIf { it != -1 }
                                                    ?: Int.MAX_VALUE // Safe fallback
                                            }
                                    }
                            )

                        userFoodRepo.updateState {
                            it.copy(
                                uiStatePager = UiStatePager(
                                    uiState = UiState.Success(revertedPagination),
                                    isLoadingMore = false
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun removeRecentlyLoggedFood(foodId: Int) {
        foodLogRepo.uiState.value.recentlyLogged.uiState
            .toSuccessOrNull()
            ?.let { pagination ->
                foodLogRepo.updateState {
                    it.copy(
                        recentlyLogged = UiStatePager(
                            uiState = UiState.Success(
                                pagination.copy(
                                    data = pagination.data.filter { it.id != foodId }
                                )
                            )
                        )
                    )
                }
            }
    }

    private val _pendingFoodFailedDeletions = mutableSetOf<Pair<Int, PendingFood>>()
    private var _pendingFoodsBeforeDeletion: List<PendingFood> = emptyList()

    fun dismissReview() {
        val idToDismiss = managers.request.reviewIdToDismiss.value ?: return

        val originalPager = pendingFoodRepo.uiState.value.pendingFoodsUiStatePager.uiState
            .toSuccessOrNull()
            ?: return

        // Store current pending foods (not already in list) before a successful dismissal
        _pendingFoodsBeforeDeletion = _pendingFoodsBeforeDeletion + run {
            originalPager.data.filter {
                it.id !in _pendingFoodsBeforeDeletion.map { p -> p.id }
            }
        }

        // Find the current pending food from the current list
        val current = originalPager.data
            .find { it.id == idToDismiss }
            ?: return

        // Store the original index/position of the dismissed pending food
        val originalIndex = _pendingFoodsBeforeDeletion
            .indexOfFirst { it.id == idToDismiss }
            .takeIf { it != -1 }
            ?: return

        // Remove the dismissed pending food from the failed deletion list.
        // This resets a previously failed dismissal so that the user can try again
        _pendingFoodFailedDeletions.removeIf { it.second.id == idToDismiss }

        pendingFoodRepo.updateState {
            it.copy(
                pendingFoodsUiStatePager = UiStatePager(
                    uiState = UiState.Success(
                        // Optimistic pagination when REMOVING (dismissing) the pending food
                        originalPager
                            .toPaginationData()
                            .calc(OptimisticUpdate.REMOVE, originalPager.getServerOffset())
                            .toResult(originalPager.data.filter { f -> f.id != idToDismiss })
                    ),
                    isLoadingMore = false
                )
            )
        }

        viewModelScope.launch {
            pendingFoodRepo.dismissReview(idToDismiss).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(reviewDismissState = state) }

                        // Reset pending foods before successful deletion if all deletions succeeded
                        if (_pendingFoodFailedDeletions.isEmpty()) {
                            _pendingFoodsBeforeDeletion = emptyList()
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(reviewDismissState = state) }

                        _pendingFoodFailedDeletions.add(originalIndex to current)

                        val currentPager = pendingFoodRepo.uiState.value.pendingFoodsUiStatePager.uiState
                            .toSuccessOrNull()
                            ?: return@collect

                        val revertedPagination = currentPager
                            .toPaginationData()
                            .calc(OptimisticUpdate.ROLLBACK, currentPager.getServerOffset())
                            .toResult(
                                // Combine current pending foods (modified after optimistic update)
                                // with the pending foods in queue to rollback
                                (currentPager.data + _pendingFoodFailedDeletions.map { it.second })
                                    .distinctBy { it.id }
                                    .sortedBy { pendingFood ->
                                        // Original position from failed deletions being rolled back
                                        _pendingFoodFailedDeletions
                                            .find { it.second.id == pendingFood.id }
                                            ?.first
                                            ?: run {
                                                // Original position from the current list of a non-dismissed
                                                // pending food
                                                _pendingFoodsBeforeDeletion
                                                    .indexOfFirst { it.id == pendingFood.id }
                                                    .takeIf { it != -1 }
                                                    ?: Int.MAX_VALUE // Safe fallback
                                            }
                                    }
                            )

                        pendingFoodRepo.updateState {
                            it.copy(
                                pendingFoodsUiStatePager = UiStatePager(
                                    uiState = UiState.Success(revertedPagination),
                                    isLoadingMore = false
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun setSelectedList(list: ListOption) {
        _selectedList.value = list
    }

    fun resetFoodRequestState() {
        _uiState.update { it.copy(foodRequestState = UiState.Idle) }
    }

    fun resetFoodAddState() {
        _uiState.update { it.copy(foodAddState = UiState.Idle) }
    }

    fun resetFoodUpdateState() {
        _uiState.update { it.copy(foodUpdateState = UiState.Idle) }
    }

    fun resetFoodDeleteState() {
        _uiState.update { it.copy(foodDeleteState = UiState.Idle) }
    }

    fun resetReviewDismissState() {
        _uiState.update { it.copy(reviewDismissState = UiState.Idle) }
    }

    /**
     * `resetFoodEditionStates` resets the following food edition related states
     * - food update state
     * - deleted nutrients
     * - nutrients in daily value map
     */
    fun resetFoodEditionStates() {
        if (_uiState.value.foodUpdateState !is UiState.Idle) resetFoodUpdateState()
        editionManager.resetAddedNutrients()
        editionManager.resetDeletedNutrients()
        resetFoodNutrientDvMap()
    }

    private fun resetFoodNutrientDvMap() {
        val nutrientDvControls = managers.creation.nutrientDvControls
        val nutrientDvMap = nutrientDvControls.nutrientDvMap.value
        if (nutrientDvMap.isNotEmpty()) nutrientDvControls.onClearData()
    }
}