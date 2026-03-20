package com.example.fitnessway.util.food.creation

import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.nutrient.INutrientDvControls
import kotlinx.coroutines.flow.StateFlow

interface IFoodCreation : INutrientDvControls {
    val formState: StateFlow<FormStates.FoodCreation>
    val currentStep: StateFlow<Int>

    val nameError: String?
    val brandError: String?
    val amountPerServingError: String?
    val servingUnitError: String?

    val isBasicDataValid: Boolean

    fun updateFormField(
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

    fun resetFormState()
}