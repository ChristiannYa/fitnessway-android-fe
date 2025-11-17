package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.ServingUnits
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.BrandInlineRules
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.NameInlineRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.brandRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.nameRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodManager : IFoodManager {
    private val _selectedFoodLog = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLog: StateFlow<FoodLogData?> = _selectedFoodLog

    private val emptyFoodCreationFormState = FormStates.FoodCreation(
        name = "",
        brand = "",
        amountPerServing = "",
        servingUnit = ""
    )

    private val _foodCreationFormState = MutableStateFlow(emptyFoodCreationFormState)
    override val foodCreationFormState: StateFlow<FormStates.FoodCreation> = _foodCreationFormState

    private val _currentStep = MutableStateFlow<Number>(1)
    override val currentStep: StateFlow<Number> = _currentStep

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
            if (value.isEmpty()) null else {
                val amount = value.toInt()

                if (amount > 0) null else "Must be greater than 0"
            }
        }

    override val formServingUnitError: String?
        get() = _foodCreationFormState.value.servingUnit.let { value ->
            if (value.isEmpty()) null else {
                val isValid = when (value) {
                    in ServingUnits.units -> true
                    else -> false
                }

                if (isValid) null else "Must be one of ${ServingUnits.units}"
            }
        }

    private val isBasicDataValid: Boolean
        get() = _foodCreationFormState.value.name.isNotEmpty() && formNameError == null &&
                formBrandError == null &&
                _foodCreationFormState.value.amountPerServing.isNotEmpty() && formAmountPerServingError == null &&
                _foodCreationFormState.value.servingUnit.isNotEmpty() && formServingUnitError == null

    override fun setSelectedFoodLog(foodLog: FoodLogData) {
        _selectedFoodLog.value = foodLog
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
                updatedNutrients[fieldName.nutrient.id] = input
                _foodCreationFormState.value.copy(nutrients = updatedNutrients)
            }
        }
    }

    override fun updateStep(
        step: Number,
        goesBack: Boolean,
        onExitForm: (() -> Unit)?
    ) {
        when (step) {
            1 -> if (goesBack) {
                _foodCreationFormState.value = emptyFoodCreationFormState
                onExitForm?.invoke()
            } else if (isCurrentStepValid(step)) _currentStep.value = 2


            2 -> if (goesBack) _currentStep.value = 1 else _currentStep.value = 3
            3 -> if (goesBack) _currentStep.value = 2 else _currentStep.value = 4
            4 -> if (goesBack) _currentStep.value = 3
        }
    }

    override fun isCurrentStepValid(step: Number): Boolean {
        return when (step) {
            1 -> isBasicDataValid
            2 -> false
            3 -> false
            4 -> false
            else -> false
        }
    }
}