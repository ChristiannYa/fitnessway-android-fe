package com.example.fitnessway.feature.lists.manager.food

import com.example.fitnessway.data.model.MFood.Enum.ServingUnits
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.Formatters.toInputDouble
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.BrandInlineRules
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.NameInlineRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.brandRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.nameRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodManager : IFoodManager {
    private val emptyFoodCreationFormState = FormStates.FoodCreation(
        name = "",
        brand = "",
        amountPerServing = "",
        servingUnit = ""
    )

    private val _foodCreationFormState = MutableStateFlow(emptyFoodCreationFormState)
    override val foodCreationFormState: StateFlow<FormStates.FoodCreation> = _foodCreationFormState

    private val _foodCreationNutrientsAsPercentages = MutableStateFlow<Map<Int, String>>(emptyMap())
    override val foodCreationNutrientsAsPercentages: StateFlow<Map<Int, String>> =
        _foodCreationNutrientsAsPercentages

    private val _currentStep = MutableStateFlow(1)
    override val currentStep: StateFlow<Int> = _currentStep

    override val createFormNameError: String?
        get() = _foodCreationFormState.value.name.let { value ->
            if (value.isEmpty()) null else {
                val result = NameInlineRules(value.trim()) checkWith nameRules
                result.exceptionOrNull()?.message
            }
        }

    override val createFormBrandError: String?
        get() = _foodCreationFormState.value.brand.let { value ->
            if (value.isEmpty()) null else {
                val result = BrandInlineRules(value.trim()) checkWith brandRules
                result.exceptionOrNull()?.message
            }
        }

    override val createFormAmountPerServingError: String?
        get() = _foodCreationFormState.value.amountPerServing.let { value ->
            validateDoubleAsString(
                doubleAsString = value,
                itemToBeValidated = "Amount Per Servings"
            )
        }

    override val createFormServingUnitError: String?
        get() = _foodCreationFormState.value.servingUnit.let { value ->
            if (value.isEmpty()) null else {
                val isValid = when (value) {
                    in ServingUnits.units -> true
                    else -> false
                }

                val servingUnits = ServingUnits.units.joinToString(separator = ", ")

                if (isValid) null else "Must be one of $servingUnits"
            }
        }

    override val isBasicDataValid: Boolean
        get() = _foodCreationFormState.value.name.isNotEmpty() && createFormNameError == null &&
                createFormBrandError == null &&
                _foodCreationFormState.value.amountPerServing.isNotEmpty() && createFormAmountPerServingError == null &&
                _foodCreationFormState.value.servingUnit.isNotEmpty() && createFormServingUnitError == null

    override fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodCreation,
        input: String
    ) {
        _foodCreationFormState.value = when (fieldName) {
            is FormFieldName.FoodCreation.BaseField -> {
                when (fieldName) {
                    FormFieldName.FoodCreation.BaseField.NAME -> {
                        _foodCreationFormState.value.copy(name = input)
                    }

                    FormFieldName.FoodCreation.BaseField.BRAND -> {
                        _foodCreationFormState.value.copy(brand = input)
                    }

                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING -> {
                        _foodCreationFormState.value.copy(amountPerServing = input)
                    }

                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT -> {
                        _foodCreationFormState.value.copy(servingUnit = input)
                    }
                }
            }

            is FormFieldName.FoodCreation.NutrientField -> {
                val updatedNutrients = _foodCreationFormState.value.nutrients.toMutableMap()
                updatedNutrients[fieldName.nutrientWithPreferences.nutrient.id] = input
                _foodCreationFormState.value.copy(nutrients = updatedNutrients)
            }
        }
    }

    override fun updateStep(
        step: Int,
        goesBack: Boolean,
        onSubmit: (() -> Unit)?,
    ) {
        when (step) {
            1 -> if (!goesBack) _currentStep.value = 2
            2 -> if (goesBack) {
                _currentStep.value = 1
            } else {
                _currentStep.value = 3
            }

            3 -> if (goesBack) {
                _currentStep.value = 2
            } else {
                _currentStep.value = 4
            }

            4 -> if (goesBack) {
                _currentStep.value = 3
            } else {
                onSubmit?.invoke()
            }
        }
    }

    override fun resetFoodFormState() {
        _currentStep.value = 1
        _foodCreationFormState.value = emptyFoodCreationFormState
    }

    override fun validateRequiredNutrients(nutrientIds: Set<Int>): Boolean {
        val formState = _foodCreationFormState.value

        val isAnyInvalid = nutrientIds.any { id ->
            val value = formState.nutrients[id]

            if (value.isNullOrEmpty()) return@any false

            value.toInputDouble() == null
        }

        if (isAnyInvalid) return false

        val hasAtLeastOnePositive = nutrientIds.any { id ->
            val value = formState.nutrients[id]
            value?.toInputDouble()?.let { it > 0 } == true
        }

        return hasAtLeastOnePositive
    }

    override fun validateOptionalNutrients(nutrientIds: Set<Int>): Boolean {
        val formState = _foodCreationFormState.value

        val isAnyInvalid = nutrientIds.any { id ->
            val value = formState.nutrients[id]

            if (value.isNullOrEmpty()) return@any false

            val amount = value.toInputDouble()
            val isInvalid = amount == null || amount <= 0.0

            isInvalid
        }

        return !isAnyInvalid
    }

    override fun addNutrientValueToPercentagesMap(nutrientId: Int, value: String) {
        val amount = _foodCreationNutrientsAsPercentages.value.toMutableMap().apply {
            put(nutrientId, value)
        }

        _foodCreationNutrientsAsPercentages.value = amount
    }

    override fun removeNutrientValueFromPercentagesMap(nutrientId: Int) {
        val removedValue = _foodCreationNutrientsAsPercentages.value.toMutableMap().apply {
            remove(nutrientId)
        }

        _foodCreationNutrientsAsPercentages.value = removedValue
    }
}