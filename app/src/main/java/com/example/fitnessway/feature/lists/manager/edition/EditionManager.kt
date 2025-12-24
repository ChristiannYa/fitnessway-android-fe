package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.ServingUnits
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.Nutrient.combineAll
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.BrandInlineRules
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.NameInlineRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.brandRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.nameRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditionManager : IEditionManager {
    private val _selectedFood = MutableStateFlow<FoodInformation?>(null)
    override val selectedFood: StateFlow<FoodInformation?> = _selectedFood

    private val _foodEditionFormState = MutableStateFlow<FormState<FormStates.FoodEdition>?>(null)
    override val foodEditionFormState: StateFlow<FormState<FormStates.FoodEdition>?> =
        _foodEditionFormState

    private val _deletedNutrients = MutableStateFlow<List<Int>>(emptyList())
    override val deletedNutrients: StateFlow<List<Int>> = _deletedNutrients

    override val editFormNameError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.name.let { value ->
                if (value.isEmpty()) null else {
                    val result = NameInlineRules(value.trim()) checkWith nameRules
                    result.exceptionOrNull()?.message
                }
            }
        }

    override val editFormBrandError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.brand.let { value ->
                if (value.isEmpty()) null else {
                    val result = BrandInlineRules(value.trim()) checkWith brandRules
                    result.exceptionOrNull()?.message
                }
            }
        }

    override val editFormAmountPerServingError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.amountPerServing.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Amount Per Servings"
                )
            }
        }

    override val editFormServingUnitError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.servingUnit.let { value ->
                if (value.isEmpty()) null else {
                    val isValid = when (value) {
                        in ServingUnits.units -> true
                        else -> false
                    }

                    if (isValid) null else "Must be one of ${ServingUnits.units}"
                }
            }
        }

    private val hasBasicNutrient: Boolean
        get() {
            val formNutrientIds = _foodEditionFormState.value?.data?.nutrients?.keys ?: return false
            val basicNutrients = _selectedFood.value?.nutrients?.basic ?: return false

            return basicNutrients.any { basicNutrientData ->
                basicNutrientData.nutrientWithPreferences.nutrient.id in formNutrientIds
            }
        }

    override val areFormNutrientsValid: Boolean
        get() = _foodEditionFormState.value?.let { formState ->
            val areAllAmountsValid = formState.data.nutrients.values.all {
                val amount = it.toDoubleOrNull()
                amount != null && amount > 0.0
            }

            areAllAmountsValid && hasBasicNutrient
        } ?: false

    override val isFormValid: Boolean
        get() = _foodEditionFormState.value?.let {
            it.data.name.isNotEmpty() && editFormNameError == null &&
                    editFormBrandError == null &&
                    it.data.amountPerServing.isNotEmpty() && editFormAmountPerServingError == null &&
                    it.data.servingUnit.isNotEmpty() && editFormServingUnitError == null &&
                    areFormNutrientsValid
        } ?: false

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }

    override fun initializeFoodForm(food: FoodInformation) {
        val nutrients = food.nutrients.combineAll().associate {
            it.nutrientWithPreferences.nutrient.id to doubleFormatter(it.amount)
        }

        // Result: {1="10.5", 2="20.3", 3="15"}
        //
        // If `.map` where to be used instead it would result in:
        // [(1, "10.5"), (2, "20.3"), (3, "15")]
        // which is a `List` but we need a map

        _foodEditionFormState.value = FormState(
            data = FormStates.FoodEdition(
                name = food.information.name,
                brand = food.information.brand ?: "",
                amountPerServing = doubleFormatter(food.information.amountPerServing),
                servingUnit = food.information.servingUnit,
                nutrients = nutrients
            )
        )
    }

    override fun updateFoodEditionFormField(
        fieldName: FormFieldName.IFoodEdition,
        input: String
    ) {
        _foodEditionFormState.value?.let { currentState ->
            val updatedData = when (fieldName) {
                is FormFieldName.FoodEdition.DetailField -> {
                    when (fieldName) {
                        FormFieldName.FoodEdition.DetailField.NAME -> {
                            currentState.data.copy(name = input)
                        }

                        FormFieldName.FoodEdition.DetailField.BRAND -> {
                            currentState.data.copy(brand = input)
                        }

                        FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING -> {
                            currentState.data.copy(amountPerServing = input)
                        }

                        FormFieldName.FoodEdition.DetailField.SERVING_UNIT -> {
                            currentState.data.copy(servingUnit = input)
                        }
                    }
                }

                is FormFieldName.FoodEdition.NutrientField -> {
                    val updatedNutrients = currentState.data.nutrients.toMutableMap()

                    updatedNutrients[fieldName.nutrient.id] = input
                    currentState.data.copy(nutrients = updatedNutrients)
                }
            }

            _foodEditionFormState.value = currentState.copy(data = updatedData)
        }
    }

    override fun filterNutrientFromForm(nutrientId: Int) {
        val currentList = _deletedNutrients.value

        if (nutrientId !in currentList) {
            _deletedNutrients.value = currentList + nutrientId
        }

        _foodEditionFormState.value?.let { formState ->
            val updatedNutrients = formState.data.nutrients.toMutableMap().apply {
                remove(nutrientId)
            }

            _foodEditionFormState.value = formState.copy(
                data = formState.data.copy(nutrients = updatedNutrients)
            )
        }
    }

    override fun resetDeletedNutrients() {
        _deletedNutrients.value = emptyList()
    }

    override fun startEditionMode() {
        _foodEditionFormState.value = _foodEditionFormState.value?.edit()
    }

    override fun simpleFormCancel() {
        _foodEditionFormState.value = _foodEditionFormState.value?.setIsEditingToFalse()
    }

    override fun cancelEditionMode() {
        _foodEditionFormState.value = _foodEditionFormState.value?.cancel()
        resetDeletedNutrients()
    }
}