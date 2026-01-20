package com.example.fitnessway.feature.lists.manager.food

import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IFoodManager {
    val foodCreationFormState: StateFlow<FormStates.FoodCreation>
    val foodCreationNutrientsAsPercentages: StateFlow<Map<Int, String>>
    val currentStep: StateFlow<Int>
    val createFormNameError: String?
    val createFormBrandError: String?
    val createFormAmountPerServingError: String?
    val createFormServingUnitError: String?
    val isBasicDataValid: Boolean

    fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodCreation,
        input: String
    )

    fun updateStep(
        step: Int,
        goesBack: Boolean = true,
        onSubmit: (() -> Unit)? = null
    )

    fun validateRequiredNutrients(nutrientIds: Set<Int>): Boolean
    fun validateOptionalNutrients(nutrientIds: Set<Int>): Boolean

    fun addNutrientValueToPercentagesMap(nutrientId: Int, value: String)
    fun removeNutrientValueFromPercentagesMap(nutrientId: Int)

    fun resetFoodFormState()
}
