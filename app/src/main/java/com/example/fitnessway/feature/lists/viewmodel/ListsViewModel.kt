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
import kotlinx.coroutines.Job
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

    private val _foodFailedUpdates = mutableListOf<FoodInformation>()

    private var originalFoodBeforeUpdate: FoodInformation? = null
    private var updateFoodFavoriteStatusJob: Job? = null

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

    fun updateFood() {
        if (this.user == null) return

        val formState = managers.edition.foodEditionFormState.value ?: return
        val selectedFood = managers.edition.selectedFood.value ?: return

        // Get current data to update optimistically
        val originalFoodsState = foodRepo.uiState.value.foodsUiState

        // Only proceed if there is data
        if (originalFoodsState !is UiState.Success) return

        // Extract data from state
        val originalFoods = originalFoodsState.data
        val originalFood = originalFoods.getFoodById(selectedFood.information.id) ?: return

        // Add failed update for this food
        // TODO: Check if we are removing the food from `_foodFailedUpdates` even after success
        _foodFailedUpdates.add(originalFood)

        // Filter out the selected food
        val foodsWithoutUpdatedFood = originalFoods.filter {
            it.information.id != selectedFood.information.id
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
        val originalNutrients = selectedFood.nutrients
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
                id = selectedFood.information.id,
                name = formState.data.name,
                brand = formState.data.brand,
                amountPerServing = formState.data.amountPerServing.toDouble(),
                servingUnit = formState.data.servingUnit
            ),
            metadata = selectedFood.metadata,
            nutrients = filteredUpdatedFoodNutrientsData
        )

        // Change the selected food value to the newly created food
        managers.edition.setSelectedFood(optimisticFood)

        // Finally inserting the new food
        val optimisticFoods = foodsWithoutUpdatedFood + optimisticFood

        // Update UI immediately
        foodRepo.updateState {
            it.copy(foodsUiState = UiState.Success(optimisticFoods))
        }

        // Create request
        val request = FoodUpdateRequest(
            information = FoodBaseInfoNullable(
                id = selectedFood.information.id,
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
                        // foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Loading -> {
                        _uiState.update { it.copy(foodUpdateState = state) }
                    }

                    is UiState.Error -> {
                        // Revert back to original state on error
                        _uiState.update { it.copy(foodUpdateState = state) }

                        // Obtain updated UI states after optimistic update
                        val currentFoodsState = foodRepo.uiState.value.foodsUiState

                        if (currentFoodsState is UiState.Success) {
                            val currentFoods = currentFoodsState.data

                            // Get the latest failed update
                            val revertedFood = _foodFailedUpdates.lastOrNull()

                            if (revertedFood != null) {
                                val revertedFoods = currentFoods.filter {
                                    it.information.id != revertedFood.information.id
                                } + revertedFood

                                managers.edition.setSelectedFood(revertedFood)

                                foodRepo.updateState {
                                    it.copy(
                                        foodsUiState = UiState.Success(revertedFoods)
                                    )
                                }

                                // Remove the latest failed update
                                _foodFailedUpdates.removeLastOrNull()
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteFood() {
        val selectedFood = managers.edition.selectedFood.value ?: return
        val selectedFoodId = selectedFood.information.id

        // Get current foods state to update optimistically
        val currentFoodsState = foodRepo.uiState.value.foodsUiState

        // Only proceed if there is data
        if (currentFoodsState !is UiState.Success) return

        // Extract current foods data from the state
        val originalFoods = currentFoodsState.data

        // Filter out optimistically the deleted food
        val optimisticFoods = originalFoods.filter {
            it.information.id != selectedFoodId
        }

        // Update UI immediately
        _uiState.update {
            it.copy(foodDeleteState = UiState.Success(selectedFood))
        }

        foodRepo.updateState {
            it.copy(foodsUiState = UiState.Success(optimisticFoods))
        }

        viewModelScope.launch {
            foodRepo.deleteFood(selectedFoodId).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodDeleteState = state) }

                        foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodDeleteState = state) }
                        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(originalFoods)) }
                    }

                    else -> {}
                }
            }
        }
    }

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
        if (originalFoodBeforeUpdate == null) {
            originalFoodBeforeUpdate = latestFood
        }

        // Cancel previous job
        updateFoodFavoriteStatusJob?.cancel()

        // Create optimistic food
        val optimisticFood = latestFood.copy(
            metadata = latestFood.metadata.copy(isFavorite = isFavorite)
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
        updateFoodFavoriteStatusJob = viewModelScope.launch {
            foodRepo.updateFoodFavoriteStatus(request).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        _uiState.update { it.copy(foodFavoriteStatusUpdateState = state) }

                        // Clear the original food before update
                        originalFoodBeforeUpdate = null
                    }

                    is UiState.Error -> {
                        _uiState.update { it.copy(foodFavoriteStatusUpdateState = state) }

                        // Revert back to original state
                        val revertedFood = originalFoodBeforeUpdate

                        if (revertedFood != null) {
                            val currentState = foodRepo.uiState.value.foodsUiState
                            if (currentState is UiState.Success) {
                                val revertedFoods = currentState.data.map { food ->
                                    if (food.information.id == revertedFood.information.id) {
                                        revertedFood
                                    } else food
                                }

                                foodRepo.updateState {
                                    it.copy(foodsUiState = UiState.Success(revertedFoods))
                                }

                                managers.edition.setSelectedFood(revertedFood)
                            }

                            originalFoodBeforeUpdate = null
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