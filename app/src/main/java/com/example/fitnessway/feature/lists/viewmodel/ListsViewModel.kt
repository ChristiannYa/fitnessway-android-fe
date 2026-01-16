package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodFavoriteStatusUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfo
import com.example.fitnessway.data.model.MFood.Model.FoodBaseInfoNullable
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MNutrient.Helpers.NutrientIdWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.food.IFoodManager
import com.example.fitnessway.feature.lists.manager.toggle.ISelectionManager
import com.example.fitnessway.util.UFood.getFoodById
import com.example.fitnessway.util.UNutrient.buildNutrientsByType
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val foodRepo: IFoodRepository,
    private val nutrientRepo: INutrientRepository,
    private val managers: IListsManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    ISelectionManager by managers.selection,
    IEditionManager by managers.edition,
    IFoodManager by managers.food {

    private val _uiState = MutableStateFlow(ListsScreenUiState())
    val uiState: StateFlow<ListsScreenUiState> = _uiState.asStateFlow()

    val foodRepoUiState = foodRepo.uiState
    val nutrientRepoUiState = nutrientRepo.uiState

    val user = userStateHolder.userState.value.user

    fun getFoods() {
        foodRepo.loadFoods()
    }

    fun getNutrients() {
        nutrientRepo.loadNutrients()
    }

    fun addFood() {
        val formState = managers.food.foodCreationFormState.value

        val request = FoodAddRequest(
            information = FoodBaseInfo(
                id = 0,
                name = formState.name,
                brand = formState.brand,
                amountPerServing = formState.amountPerServing.toDoubleOrNull() ?: 0.0,
                servingUnit = formState.servingUnit
            ),
            nutrients = formState.nutrients.filter { (_, amount) ->
                (amount.toDoubleOrNull() ?: 0.0) > 0
            }.map { (nutrientId, amount) ->
                NutrientIdWithAmount(
                    nutrientId = nutrientId, amount = amount.toDoubleOrNull() ?: 0.0
                )
            }
        )

        viewModelScope.launch {
            foodRepo.addFood(request).collect { state ->
                _uiState.update { it.copy(foodAddState = state) }
            }
        }
    }

    private var _originalFoodBeforeUpdate: FoodInformation? = null

    fun updateFood() {
        val formState = managers.edition.foodEditionFormState.value ?: return
        val selectedFoodId = managers.edition.selectedFood.value?.information?.id ?: return

        // Get current data to update optimistically
        val originalFoodsState = foodRepo.uiState.value.foodsUiState

        // Only proceed if there is ui state data
        if (originalFoodsState !is UiState.Success) return

        // Extract data from state
        val originalFoods = originalFoodsState.data

        // Obtain most recent version of the food from the repository
        val latestFood = originalFoods.getFoodById(selectedFoodId) ?: return

        // Store original food if first update
        if (_originalFoodBeforeUpdate == null) {
            _originalFoodBeforeUpdate = latestFood
        }

        // Gather updated nutrient data
        val deletedNutrients = managers.edition.deletedNutrients.value
        val upsertedNutrients = formState.data.nutrients
            .map { (nutrientId, amount) ->
                NutrientIdWithAmount(
                    nutrientId = nutrientId,
                    amount = amount.toDouble()
                )
            }

        // Create a map of all original nutrients
        val originalNutrients = latestFood.nutrients
            .combine()
            .associateBy { it.nutrientWithPreferences.nutrient.id }

        // Create updated nutrient data
        val updatedFoodNutrientData = upsertedNutrients.mapNotNull { upsertedNutrient ->
            originalNutrients[upsertedNutrient.nutrientId]?.let { originalNutrient ->
                NutrientDataWithAmount(
                    nutrientWithPreferences = originalNutrient.nutrientWithPreferences,
                    amount = upsertedNutrient.amount
                )
            }
        }

        // Filter updated nutrients by type
        val filteredUpdatedFoodNutrientsData = buildNutrientsByType(
            nutrients = updatedFoodNutrientData
        ) { type, nutrients ->
            nutrients.filter { it.nutrientWithPreferences.nutrient.type == type }
        }

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
            nutrients = filteredUpdatedFoodNutrientsData
        )

        // Create optimistic foods
        val optimisticFoods = originalFoods.map {
            if (it.information.id == latestFood.information.id) optimisticFood else it
        }

        // Update UI immediately
        _uiState.update { state ->
            state.copy(foodUpdateState = UiState.Success(optimisticFood))
        }
        foodRepo.updateState {
            it.copy(foodsUiState = UiState.Success(optimisticFoods))
        }

        managers.edition.initializeFoodForm(optimisticFood)
        managers.edition.setSelectedFood(optimisticFood)

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
                        foodRepo.clearFoodLogsUiCache()

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
                                    if (it.information.id == revertedFood.information.id) {
                                        revertedFood
                                    } else it
                                }

                                foodRepo.updateState {
                                    it.copy(
                                        foodsUiState = UiState.Success(revertedFoods)
                                    )
                                }

                                managers.edition.initializeFoodForm(revertedFood)
                                managers.edition.setSelectedFood(revertedFood)

                                // Clear the original food's data
                                _originalFoodBeforeUpdate = null
                            }
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

        _uiState.update { it.copy(foodDeleteState = UiState.Success(selectedFood)) }
        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(optimisticFoods)) }

        viewModelScope.launch {
            foodRepo.deleteFood(selectedFoodId).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodDeleteState = state) }
                        foodRepo.clearFoodLogsUiCache()

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


                                        if (failedPosition != null) {
                                            failedPosition
                                        } else {
                                            // For foods still in the list, use their absolute original position
                                            _foodsBeforeSuccessfulDeletion?.indexOfFirst {
                                                it.information.id == food.information.id
                                            } ?: Int.MAX_VALUE
                                        }
                                    }

                            foodRepo.updateState {
                                it.copy(foodsUiState = UiState.Success(data = revertedFoods))
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

    }

    private var _originalFoodBeforeFavoriteStatusUpdate: FoodInformation? = null

    fun updateFoodFavoriteStatus(isFavorite: Boolean) {
        val selectedFoodId = managers.edition.selectedFood.value?.information?.id ?: return

        // Get current foods data
        val originalFoodsState = foodRepo.uiState.value.foodsUiState

        // Proceed only if there is data
        if (originalFoodsState !is UiState.Success) return

        // Extract data from state
        val originalFoods = originalFoodsState.data

        // Obtain most recent version of the food from the repository
        val latestFood = originalFoods.getFoodById(selectedFoodId) ?: return

        // Store original food if first update
        if (_originalFoodBeforeFavoriteStatusUpdate == null) {
            _originalFoodBeforeFavoriteStatusUpdate = latestFood
        }

        // Create optimistic food
        val optimisticFood = latestFood.copy(
            metadata = latestFood.metadata.copy(
                isFavorite = isFavorite
            )
        )

        // Create optimistic foods
        val optimisticFoods = originalFoods.map { food ->
            if (food.information.id == latestFood.information.id) optimisticFood else food
        }

        // Update Ui immediately
        foodRepo.updateState {
            it.copy(foodsUiState = UiState.Success(optimisticFoods))
        }
        managers.edition.setSelectedFood(optimisticFood)

        // Send request
        val request = FoodFavoriteStatusUpdateRequest(selectedFoodId, isFavorite)
        viewModelScope.launch {
            foodRepo.updateFoodFavoriteStatus(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        val updatedFood = state.data

                        if (updatedFood.information.id == selectedFoodId) {
                            _uiState.update { it.copy(foodFavoriteStatusUpdateState = state) }
                            foodRepo.clearFoodLogsUiCache()

                            // Clear the original food's data
                            _originalFoodBeforeFavoriteStatusUpdate = null
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodFavoriteStatusUpdateState = state) }

                        // Revert back to original state
                        val revertedFood = _originalFoodBeforeFavoriteStatusUpdate

                        if (revertedFood != null) {
                            val currentFoodsState = foodRepo.uiState.value.foodsUiState
                            if (currentFoodsState is UiState.Success) {
                                val revertedFoods = currentFoodsState.data.map { food ->
                                    if (food.information.id == revertedFood.information.id) {
                                        revertedFood
                                    } else food
                                }

                                foodRepo.updateState {
                                    it.copy(foodsUiState = UiState.Success(revertedFoods))
                                }

                                managers.edition.setSelectedFood(revertedFood)
                            }

                            // Clear the original food's data
                            _originalFoodBeforeFavoriteStatusUpdate = null
                        }
                    }

                    else -> {}
                }
            }
        }
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

    fun resetFoodFavoriteStatusUpdateState() {
        _uiState.update { it.copy(foodFavoriteStatusUpdateState = UiState.Idle) }
    }

    fun resetFoodCreationScreenStates() {
        viewModelScope.launch {
            delay(500)
            resetFoodFormState()
            resetFoodAddState()
        }
    }
}