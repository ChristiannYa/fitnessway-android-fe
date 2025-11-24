package com.example.fitnessway.feature.lists.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessway.data.model.food.Food
import com.example.fitnessway.data.model.food.FoodAddNutrientAmountApiFormat
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodInformationOptionals
import com.example.fitnessway.data.model.food.FoodNutrientAmountData
import com.example.fitnessway.data.model.food.FoodUpdateRequest
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.feature.lists.manager.IListsManagers
import com.example.fitnessway.feature.lists.manager.edition.IEditionManager
import com.example.fitnessway.feature.lists.manager.toggle.ISelectionManager
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val foodRepo: IFoodRepository,
    private val managers: IListsManagers,
    userStateHolder: IUserStateHolder
) : ViewModel(),
    ISelectionManager by managers.selection,
    IEditionManager by managers.edition {

    private val _uiState = MutableStateFlow(ListsScreenUiState())
    val uiState: StateFlow<ListsScreenUiState> = _uiState.asStateFlow()

    val user = userStateHolder.userState.value.user

    fun getFoods() {
        viewModelScope.launch {
            foodRepo.getFoods().collect { state ->
                _uiState.update { it.copy(foodsState = state) }
            }
        }
    }

    fun updateFood() {
        val user = user ?: return
        val formState = managers.edition.foodEditionFormState.value ?: return
        val selectedFood = managers.edition.selectedFood.value ?: return

        // Get current data to update optimistically
        val currentFoodsState = _uiState.value.foodsState

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
            .associateBy { it.nutrient.id }

        // Insert updated nutrient data to the food
        val updatedFoodNutrientsData = upsertedNutrients.mapNotNull { upsertedNutrient ->
            originalNutrients[upsertedNutrient.nutrientId]?.let { originalNutrient ->
                FoodNutrientAmountData(
                    nutrient = originalNutrient.nutrient, // Preserve original metadata
                    amount = upsertedNutrient.amount, // Update amount
                    goal = originalNutrient.goal // Preserve original goal
                )
            }
        }

        // Filter updated nutrients by type
        val filteredUpdatedFoodNutrientsData = NutrientsByType(
            basic = updatedFoodNutrientsData.filter { it.nutrient.type == NutrientType.BASIC },
            vitamin = updatedFoodNutrientsData.filter { it.nutrient.type == NutrientType.VITAMIN },
            mineral = updatedFoodNutrientsData.filter { it.nutrient.type == NutrientType.MINERAL }
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
        _uiState.update {
            it.copy(
                foodsState = UiState.Success(optimisticFoods)
            )
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
                    }

                    is UiState.Error -> {
                        // Revert back to original state on error
                        _uiState.update {
                            it.copy(
                                foodsState = UiState.Success(currentFoods),
                                foodUpdateState = state
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun resetFoodUpdateState() {
        _uiState.update { it.copy(foodUpdateState = UiState.Idle) }
    }
}