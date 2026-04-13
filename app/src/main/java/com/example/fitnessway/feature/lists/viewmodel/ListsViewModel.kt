package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.mappers.toNutrientIdAmountList
import com.example.fitnessway.data.mappers.toPendingRequest
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toUserFoodRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfo
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MUser
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.data.repository.food_log.IFoodLogRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.pending_food.IPendingFoodRepository
import com.example.fitnessway.data.repository.user_food.IFoodRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.creation.ICreationManager
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.request.IFoodRequestManager
import com.example.fitnessway.util.UFood.getFoodById
import com.example.fitnessway.util.UNutrient
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
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
    private val foodRepo: IFoodRepository,
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
    val foodRepoUiState = foodRepo.uiState
    val nutrientRepoUiState = nutrientRepo.uiState

    val userFlow: StateFlow<MUser.Model.User?> = userStateHolder.userState
        .map { it.user }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setSelectedList(list: ListOption) {
        _selectedList.value = list
    }

    fun getNutrients() = nutrientRepo.loadNutrients()

    fun getFoods() = foodRepo.loadFoods()

    fun getMorePendingFoods() = pendingFoodRepo.loadMorePendingFoods()

    fun getPendingFoods() = pendingFoodRepo.loadPendingFoods()

    fun refreshFoods() = foodRepo.refreshFoods()

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
            foodRepo.addFood(request).collect { state ->
                _uiState.update { it.copy(foodAddState = state) }
            }
        }
    }

    private var _originalFoodBeforeUpdate: FoodInformation? = null

    fun updateFood() {
        val formState = managers.edition.foodEditionFormState.value ?: return
        val nutrientDvMap = managers.creation.nutrientDvControls.nutrientDvMap.value
        val selectedFoodId = managers.edition.selectedFood.value?.information?.id ?: return

        // Get current data to update optimistically
        val originalFoodsUiState = foodRepo.uiState.value.foodsUiState

        // Obtain nutrient data
        val nutrientsUiState = nutrientRepoUiState.value.nutrientsUiState

        // Only proceed if there is UI state data
        if (originalFoodsUiState !is UiState.Success ||
            nutrientsUiState !is UiState.Success
        ) return

        // Extract data from states
        val originalFoods = originalFoodsUiState.data
        val nutrientsWithPreferences = nutrientsUiState.data

        // Obtain most recent version of the food from the repository
        val latestFood = originalFoods.getFoodById(selectedFoodId) ?: return

        // Store original food if first update
        if (_originalFoodBeforeUpdate == null) _originalFoodBeforeUpdate = latestFood

        // Gather updated nutrient data
        val addedNutrients = managers.edition.addedNutrients.value
        val deletedNutrients = managers.edition.deletedNutrients.value
        val upsertedNutrients = formState.data.nutrients.toNutrientIdAmountList(nutrientDvMap)

        // Obtain added nutrients if they are present
        val addedNutrientsWithPreferences = addedNutrients.mapNotNull { addedNutrient ->
            nutrientsWithPreferences.combine().find { it.nutrient.id == addedNutrient.id }
        }

        // Create a list of all original nutrients and added nutrients (if any)'s preferences metadata
        val allNutrientsWithPreferences = (latestFood.nutrients
            .combine()
            .map { it.nutrientWithPreferences } + addedNutrientsWithPreferences)
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
        val updatedNutrientsByType = UNutrient.buildNutrientsByType2(
            nutrients = updatedFoodNutrientData,
            getType = { it.nutrientWithPreferences.nutrient.type }
        )

        // Create the new food
        val optimisticFood = FoodInformation(
            information = FoodBaseInfo(
                id = latestFood.information.id,
                name = formState.data.name,
                brand = formState.data.brand,
                amountPerServing = formState.data.amountPerServing.toDouble(),
                servingUnit = formState.data.servingUnit
            ),
            metadata = latestFood.metadata,
            nutrients = updatedNutrientsByType
        )

        // Create optimistic data
        val optimisticFoods = originalFoods.map {
            if (it.information.id == latestFood.information.id) optimisticFood else it
        }

        // Update UI immediately
        managers.edition.setSelectedFood(optimisticFood)
        managers.edition.initializeFoodForm(optimisticFood)
        resetFoodEditionStates()

        _uiState.update { it.copy(foodUpdateState = UiState.Success(optimisticFood)) }
        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(optimisticFoods)) }
        removeRecentlyLoggedFood(latestFood.information.id)

        // Create request
        val request = FoodUpdateRequest(
            information = FoodBaseInfoNullable(
                id = latestFood.information.id,
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
            foodRepo.updateFood(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodUpdateState = state) }
                        foodLogRepo.clearFoodLogsUiCache()

                        // Clear the original food's data
                        _originalFoodBeforeUpdate = null
                    }

                    is UiState.Error -> {
                        // Provide ui the error state
                        _uiState.update { it.copy(foodUpdateState = state) }

                        val revertedFood = _originalFoodBeforeUpdate

                        if (revertedFood != null) {
                            // Obtain updated UI states after optimistic update
                            val currentFoodsState = foodRepo.uiState.value.foodsUiState

                            if (currentFoodsState is UiState.Success) {
                                val currentFoods = currentFoodsState.data

                                val revertedFoods = currentFoods.map {
                                    if (it.information.id == revertedFood.information.id) revertedFood else it
                                }

                                foodRepo.updateState { it.copy(foodsUiState = UiState.Success(revertedFoods)) }

                                managers.edition.initializeFoodForm(revertedFood)
                                managers.edition.setSelectedFood(revertedFood)

                                // Clear the original food's data
                                _originalFoodBeforeUpdate = null
                            }
                        }

                        if (foodLogRepo.uiState.value.recentlyLogged.uiState.hasState) {
                            foodLogRepo.refreshRecentlyLogged() // Unsafe to manually revert
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private val _foodFailedDeletions = mutableSetOf<Pair<Int, FoodInformation>>()
    private var _foodsBeforeSuccessfulDeletion: List<FoodInformation>? = null

    fun deleteFood() {
        val selectedFood = managers.edition.selectedFood.value ?: return

        val selectedFoodId = selectedFood.information.id
        val originalFoodsState = foodRepo.uiState.value.foodsUiState

        if (originalFoodsState !is UiState.Success) return

        val originalFoods = originalFoodsState.data

        // Capture the foods before successful deletion on first deletion
        if (_foodsBeforeSuccessfulDeletion == null) {
            _foodsBeforeSuccessfulDeletion = originalFoods
        }

        val latestFood = originalFoods.getFoodById(selectedFoodId) ?: return

        // Use the foods before successful list for position
        val originalPosition = _foodsBeforeSuccessfulDeletion?.indexOfFirst {
            it.information.id == selectedFoodId
        } ?: return

        _foodFailedDeletions.removeIf { it.second.information.id == selectedFoodId }

        val optimisticFoods = originalFoods.filter {
            it.information.id != selectedFoodId
        }

        // Handle recently logged list update if it's available
        removeRecentlyLoggedFood(latestFood.information.id)
        _uiState.update { it.copy(foodDeleteState = UiState.Success(selectedFood)) }
        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(optimisticFoods)) }

        viewModelScope.launch {
            foodRepo.deleteFood(selectedFoodId).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodDeleteState = state) }
                        foodLogRepo.clearFoodLogsUiCache()

                        // Reset foods before successful deletion if all deletions succeeded
                        if (_foodFailedDeletions.isEmpty()) {
                            _foodsBeforeSuccessfulDeletion = null
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodDeleteState = state) }

                        // Add failed deletion
                        _foodFailedDeletions.add(originalPosition to latestFood)

                        val currentFoodsState = foodRepo.uiState.value.foodsUiState

                        if (currentFoodsState is UiState.Success) {
                            val currentFoods = currentFoodsState.data

                            val revertedFoods =
                                (currentFoods + _foodFailedDeletions.map { it.second })
                                    .distinctBy { it.information.id }
                                    .sortedBy { food ->
                                        // First check failed deletions
                                        val failedPosition = _foodFailedDeletions.find {
                                            it.second.information.id == food.information.id
                                        }?.first


                                        failedPosition ?: (
                                                // For foods still in the list, use their absolute original position
                                                _foodsBeforeSuccessfulDeletion?.indexOfFirst {
                                                    it.information.id == food.information.id
                                                } ?: Int.MAX_VALUE)
                                    }

                            foodRepo.updateState {
                                it.copy(foodsUiState = UiState.Success(data = revertedFoods))
                            }
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