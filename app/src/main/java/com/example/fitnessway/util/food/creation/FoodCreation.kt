package com.example.fitnessway.util.food.creation

import com.example.fitnessway.data.model.m_26.FoodSource
import com.example.fitnessway.data.model.m_26.ServingUnit
import com.example.fitnessway.util.Formatters
import com.example.fitnessway.util.Formatters.toInputDouble
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.InlineRules
import com.example.fitnessway.util.form.field.Rules
import com.example.fitnessway.util.isValidEnum
import com.example.fitnessway.util.listEnumValues
import com.example.fitnessway.util.nutrient.NutrientDvControls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class FoodCreation(val foodSource: FoodSource) : IFoodCreation, NutrientDvControls() {

    private val emptyFoodCreationFormState = FormStates.FoodCreation(
        name = "",
        brand = "",
        amountPerServing = "",
        servingUnit = ""
    )

    private val _foodCreationFormState = MutableStateFlow(emptyFoodCreationFormState)
    override val formState: StateFlow<FormStates.FoodCreation> = _foodCreationFormState

    private val _currentStep = MutableStateFlow(1)
    override val currentStep: StateFlow<Int> = _currentStep

    override val nameError: String?
        get() = _foodCreationFormState.value.name.let { value ->
            if (value.isEmpty()) null else {
                val result =
                    InlineRules.FoodCreation.NameInlineRules(value.trim()) checkWith Rules.FoodCreation.nameRules
                result.exceptionOrNull()?.message
            }
        }

    override val brandError: String?
        get() = _foodCreationFormState.value.brand.let { value ->
            if (value.isEmpty()) null else {
                val rules = when (foodSource) {
                    FoodSource.USER -> Rules.FoodCreation.userBrandRules
                    FoodSource.APP -> Rules.FoodCreation.appBrandRules
                }

                val result = InlineRules.FoodCreation.BrandInlineRules(value.trim()) checkWith rules
                result.exceptionOrNull()?.message
            }
        }

    override val amountPerServingError: String?
        get() = _foodCreationFormState.value.amountPerServing.let { value ->
            Formatters.validateDoubleAsString(
                doubleAsString = value,
                itemToBeValidated = "Amount Per Servings"
            )
        }

    override val servingUnitError: String?
        get() = _foodCreationFormState.value.servingUnit.let { value ->
            if (value.isEmpty()) null else {
                if (value.isValidEnum<ServingUnit>()) null else {
                    "Must be one of ${listEnumValues<ServingUnit>()}"
                }
            }
        }

    override val isBasicDataValid: Boolean
        get() = _foodCreationFormState.value.name.isNotEmpty() && nameError == null &&
                (foodSource == FoodSource.USER && brandError == null ||
                        foodSource == FoodSource.APP &&
                        _foodCreationFormState.value.brand.isNotEmpty() &&
                        brandError == null) &&
                _foodCreationFormState.value.amountPerServing.isNotEmpty() && amountPerServingError == null &&
                _foodCreationFormState.value.servingUnit.isNotEmpty() && servingUnitError == null

    override fun updateFormField(
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

    override fun resetFormState() {
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
}