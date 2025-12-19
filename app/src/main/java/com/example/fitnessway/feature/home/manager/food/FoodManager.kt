package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.ServingUnits
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.util.Formatters.logcat
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

    private val _currentStep = MutableStateFlow(1)
    override val currentStep: StateFlow<Int> = _currentStep

    override val formNameError: String?
        get() = _foodCreationFormState.value.name.let { value ->
            if (value.isEmpty()) null else {
                val result = NameInlineRules(value.trim()) checkWith nameRules
                result.exceptionOrNull()?.message
            }
        }

    override val formBrandError: String?
        get() = _foodCreationFormState.value.brand.let { value ->
            if (value.isEmpty()) null else {
                val result = BrandInlineRules(value.trim()) checkWith brandRules
                result.exceptionOrNull()?.message
            }
        }

    override val formAmountPerServingError: String?
        get() = _foodCreationFormState.value.amountPerServing.let { value ->
            validateDoubleAsString(
                doubleAsString = value,
                itemToBeValidated = "Amount Per Servings"
            )
        }

    override val formServingUnitError: String?
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
        get() = _foodCreationFormState.value.name.isNotEmpty() && formNameError == null &&
                formBrandError == null &&
                _foodCreationFormState.value.amountPerServing.isNotEmpty() && formAmountPerServingError == null &&
                _foodCreationFormState.value.servingUnit.isNotEmpty() && formServingUnitError == null

    override val areBasicNutrientsValid: Boolean
        get() = _foodCreationFormState.value.nutrients.values.any {
            (it.toDoubleOrNull() ?: 0.0) > 0
        }

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

    override fun resetFoodFormState() {
        _currentStep.value = 1
        _foodCreationFormState.value = emptyFoodCreationFormState
    }

    override fun updateStep(
        step: Int,
        goesBack: Boolean,
        onExitForm: (() -> Unit)?,
        onSubmit: (() -> Unit)?,
    ) {
        when (step) {
            1 -> if (goesBack) {
                onExitForm?.invoke()
                resetFoodFormState()
            } else if (isBasicDataValid) {
                _currentStep.value = 2
            }

            2 -> if (goesBack) _currentStep.value =
                1 else if (areBasicNutrientsValid) _currentStep.value = 3

            3 -> if (goesBack) _currentStep.value = 2 else _currentStep.value = 4
            4 -> if (goesBack) _currentStep.value = 3 else {
                onSubmit?.invoke()
            }
        }
    }

    override fun validateFoodNonBaseNutrients(nutrients: List<Nutrient>): Boolean {
        val formState = _foodCreationFormState.value
        val targetNutrientIds = nutrients.map { it.id }.toSet()

        val isAnyTargetNutrientInvalid = targetNutrientIds.any { id ->
            val value = formState.nutrients[id]

            if (value.isNullOrEmpty()) return@any false

            val amount = value.toInputDouble()
            val isInvalid = amount == null || amount <= 0.0
            isInvalid
        }

        return !isAnyTargetNutrientInvalid
    }
}