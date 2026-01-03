package com.example.fitnessway.feature.lists.manager.edition

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.ServingUnits
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.UNutrient.combine
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

    private val _originalFormName = MutableStateFlow<String?>(null)
    private val _originalFormBrand = MutableStateFlow<String?>(null)
    private val _originalAmountPerServing = MutableStateFlow<String?>(null)
    private val _originalServingUnit = MutableStateFlow<String?>(null)
    private val _originalNutrients = MutableStateFlow<Map<Int, String>?>(null)

    private val _editFormNameError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.name.let { value ->
                if (value.isEmpty()) null else {
                    val result = NameInlineRules(value.trim()) checkWith nameRules
                    result.exceptionOrNull()?.message
                }
            }
        }

    private val _editFormBrandError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.brand.let { value ->
                if (value.isEmpty()) null else {
                    val result = BrandInlineRules(value.trim()) checkWith brandRules
                    result.exceptionOrNull()?.message
                }
            }
        }

    private val _editFormAmountPerServingError: String?
        get() = _foodEditionFormState.value?.let { formState ->
            formState.data.amountPerServing.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Amount Per Servings"
                )
            }
        }

    private val _editFormServingUnitError: String?
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

    override val isFormValid: Boolean
        get() = _foodEditionFormState.value?.let { formState ->
            val hasAnyChange = (formState.data.name != _originalFormName.value) ||
                    (formState.data.brand != _originalFormBrand.value) ||
                    (formState.data.amountPerServing != _originalAmountPerServing.value) ||
                    (formState.data.servingUnit != _originalServingUnit.value) ||
                    formState.data.nutrients.any { (id, value) ->
                        _originalNutrients.value?.get(id) != value
                    } ||
                    (_originalNutrients.value?.any { (id, _) ->
                        !formState.data.nutrients.contains(id)
                    } ?: false)

            val hasNoErrors = _editFormNameError == null &&
                    _editFormBrandError == null &&
                    _editFormAmountPerServingError == null &&
                    _editFormServingUnitError == null

            val requiredFieldsProvided = formState.data.name.isNotEmpty() &&
                    formState.data.amountPerServing.isNotEmpty() &&
                    formState.data.servingUnit.isNotEmpty()

            val nutrientsAreValid = run {
                val formNutrients = formState.data.nutrients

                val areAllAmountsValid = formNutrients.values.all {
                    val amount = it.toDoubleOrNull()
                    amount != null && amount > 0.0
                }

                val basicNutrients = _selectedFood.value?.nutrients?.basic ?: return@let false

                val hasBasicNutrient = basicNutrients.any {
                    it.nutrientWithPreferences.nutrient.id in formNutrients.keys
                }

                areAllAmountsValid && hasBasicNutrient
            }

            hasAnyChange && hasNoErrors && requiredFieldsProvided && nutrientsAreValid
        } ?: false

    override fun initializeFoodForm(food: FoodInformation) {
        val nutrients = food.nutrients.combine().associate {
            it.nutrientWithPreferences.nutrient.id to doubleFormatter(it.amount, 4)
        }

        _originalFormName.value = food.information.name
        _originalFormBrand.value = food.information.brand ?: ""
        _originalAmountPerServing.value = doubleFormatter(
            value = food.information.amountPerServing,
            decimalPlaces = 4
        )
        _originalServingUnit.value = food.information.servingUnit
        _originalNutrients.value = nutrients

        _foodEditionFormState.value = FormState(
            data = FormStates.FoodEdition(
                name = food.information.name,
                brand = food.information.brand ?: "",
                amountPerServing = doubleFormatter(food.information.amountPerServing, 4),
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

    override fun setSelectedFood(food: FoodInformation) {
        _selectedFood.value = food
    }

    override fun resetDeletedNutrients() {
        _deletedNutrients.value = emptyList()
    }

    override fun simpleFormCancel() {
        _foodEditionFormState.value = _foodEditionFormState.value?.setIsEditingToFalse()
    }
}