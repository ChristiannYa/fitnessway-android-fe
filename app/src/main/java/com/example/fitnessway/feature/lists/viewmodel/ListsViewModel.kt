package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.mappers.toEdibleType
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
import com.example.fitnessway.data.mappers.toUserEdibleRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.model.m_26.EdibleBase
import com.example.fitnessway.data.model.m_26.EdibleListFilter
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.OptimisticUpdate
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.repository.edible_list.food.IUserFoodRepository
import com.example.fitnessway.data.repository.edible_list.supplement.IUserSupplementRepository
import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.food.IFoodRecentLog
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.pending.food.IPendingFoodRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.creation.ICreationManager
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.request.IEdibleRequestManager
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
    private val foodLogRepo: IEdibleLogRepository,
    private val foodRecentLogRepo: IFoodRecentLog,
    private val nutrientRepo: INutrientRepository,
    private val managers: IListsManagers,
    userStateHolder: IUserStateHolder
) : ViewModel() {

    init {
        managers.edition.init(viewModelScope)
    }

    val editionManager: IEditionManager get() = managers.edition
    val creationManager: ICreationManager get() = managers.creation
    val requestManager: IEdibleRequestManager get() = managers.request

    private val _uiState = MutableStateFlow(ListsScreenUiState())
    val uiState: StateFlow<ListsScreenUiState> = _uiState.asStateFlow()

    private val _edibleListFilter = MutableStateFlow<EdibleListFilter>(EdibleListFilter.FOOD)
    val edibleListFilter: StateFlow<EdibleListFilter> = _edibleListFilter

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

    fun getFoods() = userFoodRepo.load()
    fun getMoreFoods() = userFoodRepo.loadMore()

    fun getSupplements() = userSupplementRepo.load()
    fun getMoreSupplements() = userSupplementRepo.loadMore()

    fun getPendingFoods() = pendingFoodRepo.load()
    fun getMorePendingFoods() = pendingFoodRepo.loadMore()

    fun addFoodRequest() {
        val formState = managers.request.formState.value

        val nutrientDvMap = managers.request.nutrientDvControls.nutrientDvMap.value
        val nutrients = formState.nutrients.toNutrientIdAmountList(nutrientDvMap)
        val request = formState.toPendingRequest(nutrients, EdibleType.FOOD.name)

        viewModelScope.launch {
            pendingFoodRepo.add(request).collect { state ->
                _uiState.update { it.copy(foodRequestState = state) }

                if (state is UiState.Success) pendingFoodRepo.refresh()
            }
        }
    }

    fun addEdible() {
        val formState = managers.creation.formState.value
        val edibleType = edibleListFilter.value.toEdibleType()

        val request = formState.toUserEdibleRequest(
            nutrients = managers.creation.nutrientDvControls.nutrientDvMap.value
                .let { dvMap -> formState.nutrients.toNutrientIdAmountList(dvMap) },
            edibleType = edibleType.name
        )

        viewModelScope.launch {
            userFoodRepo.add(request).collect { state ->
                _uiState.update { it.copy(foodAddState = state) }

                if (state is UiState.Success) {
                    when (edibleType) {
                        EdibleType.FOOD -> userFoodRepo.refresh()
                        EdibleType.SUPPLEMENT -> userSupplementRepo.refresh()
                    }
                }
            }
        }
    }

    private var _originalEdiblesBeforeUpdate = mutableSetOf<UserEdible>()

    fun updateEdible() {
        val formState = managers.edition.edibleEditionFormState.value ?: return
        val nutrientDvMap = managers.creation.nutrientDvControls.nutrientDvMap.value
        val selectedFoodId = managers.edition.selectedEdible.value?.id ?: return

        val edibleType = edibleListFilter.value.toEdibleType()

        // Get current data to update optimistically
        val originalPager = edibleType
            .getUserEdiblePaginationOrNull()
            ?: return

        // Obtain nutrient data
        val originalNutrients = nutrientRepoUiState.value.nutrientsUiState
            .toSuccessOrNull()
            ?: return

        // Obtain most recent version of the food from the repository
        val latestFood = originalPager.data
            .find { it.id == selectedFoodId }
            ?: return

        // Add if this edible isn't already being tracked
        if (_originalEdiblesBeforeUpdate.none { it.id == latestFood.id }) {
            _originalEdiblesBeforeUpdate.add(latestFood)
        }

        // Gather updated nutrient data
        val addedNutrients = managers.edition.addedNutrients.value
        val deletedNutrients = managers.edition.deletedNutrients.value
        val upsertedNutrients = formState.data.nutrients.toNutrientIdAmountList(nutrientDvMap)

        // Obtain added nutrients if they are present
        val addedNutrientsWithPreferences = addedNutrients.mapNotNull { addedNutrient ->
            originalNutrients
                .combine()
                .find { it.nutrient.id == addedNutrient.id }
        }

        // Create a list of all original nutrients and added nutrients (if any)'s preferences metadata
        val allNutrientsWithPreferences = (latestFood.information.nutrients
            .toList()
            .map {
                it.toM25NutrientDataWithAmount().nutrientWithPreferences
            } + addedNutrientsWithPreferences)
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
        val optimisticFood = latestFood.copy(
            information = com.example.fitnessway.data.model.m_26.EdibleInformation(
                base = EdibleBase(
                    formState.data.name,
                    brand = formState.data.brand,
                    amountPerServing = formState.data.amountPerServing.toDouble(),
                    servingUnit = formState.data.servingUnit.toEnum()
                ),
                nutrients = updatedNutrientsByType
            )
        )

        // Create optimistic data
        val optimisticFoods = originalPager.data.map {
            if (it.id == latestFood.id) optimisticFood else it
        }

        // Update UI immediately
        managers.edition.setSelectedEdible(optimisticFood)
        managers.edition.initializeEdibleForm(optimisticFood)
        resetFoodEditionStates()

        _uiState.update { it.copy(foodUpdateState = UiState.Success(Unit)) }

        val optimisticPager = UiStatePager(
            uiState = UiState.Success(
                originalPager.copy(
                    data = optimisticFoods
                )
            ),
            isLoadingMore = false
        )

        when (edibleType) {
            EdibleType.SUPPLEMENT -> userSupplementRepo.update { it.copyWithPager(optimisticPager) }
            EdibleType.FOOD -> userFoodRepo.update { it.copyWithPager(optimisticPager) }
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
            when (edibleType) {
                EdibleType.SUPPLEMENT -> userSupplementRepo.update(request)
                EdibleType.FOOD -> userFoodRepo.update(request)
            }.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodUpdateState = UiState.Success(Unit)) }
                        foodLogRepo.clearMappedDates()

                        // Clear the original food's data
                        _originalEdiblesBeforeUpdate.removeIf { it.id == selectedFoodId }
                    }

                    is UiState.Error -> {
                        // Provide ui the error state
                        _uiState.update { it.copy(foodUpdateState = state) }

                        val revertedFood = _originalEdiblesBeforeUpdate
                            .find { it.id == selectedFoodId }

                        if (revertedFood != null) {
                            val currentFoodsPager = edibleType
                                .getUserEdiblePaginationOrNull()
                                ?: return@collect

                            val revertedFoods = currentFoodsPager.data.map {
                                if (it.id == revertedFood.id) revertedFood else it
                            }

                            val revertedPager = UiStatePager(
                                uiState = UiState.Success(
                                    currentFoodsPager.copy(
                                        data = revertedFoods
                                    )
                                )
                            )

                            when (edibleType) {
                                EdibleType.SUPPLEMENT -> userSupplementRepo.update { it.copy(uiStatePager = revertedPager) }
                                EdibleType.FOOD -> userFoodRepo.update { it.copy(uiStatePager = revertedPager) }
                            }

                            managers.edition.initializeEdibleForm(revertedFood)
                            managers.edition.setSelectedEdible(revertedFood)
                        }

                        if (foodRecentLogRepo.uiState.value.uiStatePager.uiState.hasState) {
                            foodRecentLogRepo.refresh()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun removeRecentlyLoggedFood(foodId: Int) {
        foodRecentLogRepo.uiState.value.uiStatePager.uiState
            .toSuccessOrNull()
            ?.let { pagination ->
                foodRecentLogRepo.updateState {
                    it.copy(
                        uiStatePager = UiStatePager(
                            uiState = UiState.Success(
                                pagination.copy(data = pagination.data.filter { f -> f.id != foodId })
                            )
                        )
                    )
                }
            }
    }

    private val _edibleFailedDeletions = mutableMapOf<EdibleType, MutableSet<Pair<Int, UserEdible>>>()
    private var _ediblesBeforeDeletion = mutableMapOf<EdibleType, List<UserEdible>>()

    fun deleteEdible() {
        val edibleToRemove = managers.edition.selectedEdible.value ?: return

        val edibleType = edibleListFilter.value.toEdibleType()

        val originalPager = edibleType
            .getUserEdiblePaginationOrNull()
            ?: return

        // Store current foods (not already in list) before a successful dismissal
        _ediblesBeforeDeletion
            .getOrDefault(edibleType, emptyList())
            .let { ediblesBeforeDeletion ->
                _ediblesBeforeDeletion[edibleType] = ediblesBeforeDeletion + originalPager.data.filter {
                    it.id !in ediblesBeforeDeletion.map { f -> f.id }
                }
            }

        // Find the current food from the current list
        val current = originalPager.data
            .find { it.id == edibleToRemove.id }
            ?: return

        // Store the original index/position of the food removed
        val originalIndex = _ediblesBeforeDeletion[edibleType]
            ?.indexOfFirst { it.id == edibleToRemove.id }
            ?.takeIf { it != -1 }
            ?: return

        // Remove the food from the failed deletion list.
        // This resets a previously failed dismissal so that the user can try again
        _edibleFailedDeletions[edibleType]
            ?.removeIf { it.second.id == edibleToRemove.id }

        // Update states optimistically
        _uiState.update { it.copy(edibleDeleteState = UiState.Success(Unit)) }

        val optimisticPager = UiStatePager(
            uiState = UiState.Success(
                originalPager
                    .toPaginationData()
                    .calc(OptimisticUpdate.REMOVE, originalPager.getServerOffset())
                    .toResult(originalPager.data.filter { f -> f.id != edibleToRemove.id })
            ),
            isLoadingMore = false
        )

        when (edibleType) {
            EdibleType.SUPPLEMENT -> userSupplementRepo.update { it.copyWithPager(optimisticPager) }
            else -> userFoodRepo.update { it.copyWithPager(optimisticPager) }
        }

        viewModelScope.launch {
            when (edibleType) {
                EdibleType.SUPPLEMENT -> userSupplementRepo.delete(edibleToRemove.id)
                else -> userFoodRepo.delete(edibleToRemove.id)
            }.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(edibleDeleteState = UiState.Success(Unit)) }

                        // Reset foods before successful deletion if all deletions succeeded
                        if (_edibleFailedDeletions[edibleType].isNullOrEmpty()) {
                            _ediblesBeforeDeletion.remove(edibleType)
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(edibleDeleteState = state) }

                        _edibleFailedDeletions
                            .getOrPut(edibleType) { mutableSetOf() }
                            .add(originalIndex to current)

                        val currentPager = edibleType
                            .getUserEdiblePaginationOrNull()
                            ?: return@collect

                        val revertedPager = (_edibleFailedDeletions[edibleType])
                            ?.let { failedDeletions ->
                                UiStatePager(
                                    uiState = UiState.Success(
                                        currentPager
                                            .toPaginationData()
                                            .calc(OptimisticUpdate.ROLLBACK, currentPager.getServerOffset())
                                            .toResult(
                                                // Combine the current foods (modified after optimistic update)
                                                // with the foods in the queue to rollback
                                                (currentPager.data + failedDeletions.map { it.second })
                                                    .distinctBy { it.id }
                                                    .sortedBy { food ->
                                                        failedDeletions
                                                            // Original position from failed deletion being rolled back
                                                            .find { it.second.id == food.id }
                                                            ?.first
                                                            ?: run {
                                                                // Original position from the current list of a
                                                                // non-dismissed food
                                                                _ediblesBeforeDeletion[edibleType]
                                                                    ?.indexOfFirst { it.id == food.id }
                                                                    ?.takeIf { it != -1 }
                                                                    ?: Int.MAX_VALUE // Safe fallback
                                                            }
                                                    }
                                            )
                                    ),
                                    isLoadingMore = false
                                )
                            }
                            ?: return@collect

                        when (edibleType) {
                            EdibleType.SUPPLEMENT -> userSupplementRepo.update { it.copyWithPager(revertedPager) }
                            else -> userFoodRepo.update { it.copyWithPager(revertedPager) }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private val _pendingFoodFailedDeletions = mutableSetOf<Pair<Int, PendingFood>>()
    private var _pendingFoodsBeforeDeletion: List<PendingFood> = emptyList()

    fun dismissReview() {
        val idToDismiss = managers.request.reviewIdToDismiss.value ?: return

        val originalPager = pendingFoodRepo.uiState.value.uiStatePager.uiState
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

        pendingFoodRepo.update {
            it.copy(
                uiStatePager = UiStatePager(
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
            pendingFoodRepo.dismiss(idToDismiss).collect { state ->
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

                        val currentPager = pendingFoodRepo.uiState.value.uiStatePager.uiState
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

                        pendingFoodRepo.update {
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

    fun setListType(list: EdibleListFilter) {
        _edibleListFilter.value = list
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
        _uiState.update { it.copy(edibleDeleteState = UiState.Idle) }
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

    /**
     * Returns either the user's supplement or food repository depending on
     * the edible type
     */
    private fun EdibleType.getUserEdiblePaginationOrNull() = when (this) {
        EdibleType.FOOD -> userFoodRepo.uiState.value.uiStatePager
        EdibleType.SUPPLEMENT -> userSupplementRepo.uiState.value.uiStatePager
    }.toPaginationOrNull()
}