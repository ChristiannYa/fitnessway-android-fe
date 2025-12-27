package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessway.data.model.food.Food
import com.example.fitnessway.data.model.food.FoodAddInfoApiFormat
import com.example.fitnessway.data.model.food.FoodAddNutrientAmountApiFormat
import com.example.fitnessway.data.model.food.FoodAddRequest
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodInformationOptionals
import com.example.fitnessway.data.model.food.FoodUpdateRequest
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.food.IFoodManager
import com.example.fitnessway.feature.lists.manager.toggle.ISelectionManager
import com.example.fitnessway.util.Formatters.logcat
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
        val user = user ?: return
        val formState = managers.food.foodCreationFormState.value

        // @TODO: Make the request optimistic

        val request = FoodAddRequest(
            userId = user.id,
            information = FoodAddInfoApiFormat(
                name = formState.name,
                brand = formState.brand,
                amountPerServing = formState.amountPerServing.toDoubleOrNull() ?: 0.0,
                servingUnit = formState.servingUnit
            ),
            nutrients = formState.nutrients.filter { (_, amount) ->
                (amount.toDoubleOrNull() ?: 0.0) > 0
            }.map { (nutrientId, amount) ->
                FoodAddNutrientAmountApiFormat(
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
        val user = user ?: return
        val formState = managers.edition.foodEditionFormState.value ?: return
        val selectedFood = managers.edition.selectedFood.value ?: return

        // Get current data to update optimistically
        val currentFoodsState = foodRepo.uiState.value.foodsUiState

        // Only proceed if there is data
        if (currentFoodsState !is UiState.Success) return

        // Extract current foods state
        val currentFoods = currentFoodsState.data

        // Optimistically update foods by filtering out the updated food
        val optimisticFoodsWithoutUpdatedFood = currentFoods.filter {
            it.information.id != selectedFood.information.id
        }

        // Gather new nutrient updated nutrient data
        val deletedNutrients = managers.edition.deletedNutrients.value
        val upsertedNutrients = formState.data.nutrients
            .map { (nutrientId, amount) ->
                FoodAddNutrientAmountApiFormat(
                    nutrientId = nutrientId,
                    amount = amount.toDouble()
                )
            }

        // Create a map of all original nutrients
        val originalNutrients = (selectedFood.nutrients.basic +
                selectedFood.nutrients.vitamin +
                selectedFood.nutrients.mineral)
            .associateBy { it.nutrientWithPreferences.nutrient.id }

        // Insert updated nutrient data to the food
        val updatedFoodNutrientsData = upsertedNutrients.mapNotNull { upsertedNutrient ->
            originalNutrients[upsertedNutrient.nutrientId]?.let { originalNutrient ->
                NutrientAmountData(
                    nutrientWithPreferences = originalNutrient.nutrientWithPreferences,
                    amount = upsertedNutrient.amount // Update amount
                )
            }
        }

        // Filter updated nutrients by type
        val filteredUpdatedFoodNutrientsData = NutrientsByType(
            basic = updatedFoodNutrientsData.filter { it.nutrientWithPreferences.nutrient.type == NutrientType.BASIC },
            vitamin = updatedFoodNutrientsData.filter { it.nutrientWithPreferences.nutrient.type == NutrientType.VITAMIN },
            mineral = updatedFoodNutrientsData.filter { it.nutrientWithPreferences.nutrient.type == NutrientType.MINERAL }
        )

        // Create the new food
        val optimisticFood = FoodInformation(
            information = Food(
                id = selectedFood.information.id,
                name = formState.data.name,
                brand = formState.data.brand,
                amountPerServing = formState.data.amountPerServing.toDouble(),
                servingUnit = formState.data.servingUnit
            ),
            nutrients = filteredUpdatedFoodNutrientsData
        )

        // Change the selected food value to the newly created food
        managers.edition.setSelectedFood(optimisticFood)

        // Finally inserting the new food
        val optimisticFoods = optimisticFoodsWithoutUpdatedFood + optimisticFood

        // Update UI immediately
        foodRepo.updateState {
            it.copy(foodsUiState = UiState.Success(optimisticFoods))
        }

        val request = FoodUpdateRequest(
            userId = user.id,
            information = FoodInformationOptionals(
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

                        foodRepo.clearFoodLogsUiCache()
                    }

                    is UiState.Error -> {
                        // Revert back to original state on error
                        _uiState.update { it.copy(foodUpdateState = state) }
                        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(currentFoods)) }
                        managers.edition.setSelectedFood(selectedFood)
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
        val currentFoods = currentFoodsState.data

        // Filter out optimistically the deleted food
        val optimisticFoods = currentFoods.filter {
            it.information.id != selectedFoodId
        }

        // Update UI immediately
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
                        foodRepo.updateState { it.copy(foodsUiState = UiState.Success(currentFoods)) }
                    }

                    else -> {}
                }
            }
        }
    }

    fun resetFoodAddState() {
        logcat("lists view model resetFoodAddState called")
        _uiState.update { it.copy(foodAddState = UiState.Idle) }
    }

    fun resetFoodUpdateState() {
        _uiState.update { it.copy(foodUpdateState = UiState.Idle) }
    }

    fun resetFoodDeleteState() {
        _uiState.update { it.copy(foodDeleteState = UiState.Idle) }
    }

    fun resetFoodCreationScreenStatesOnSuccess() {
        viewModelScope.launch {
            delay(500)
            resetFoodFormState()
            resetFoodAddState()
        }
    }
}