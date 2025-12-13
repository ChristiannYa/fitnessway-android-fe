package com.example.fitnessway.feature.home.manager.food

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.StateFlow

interface IFoodManager {
    val foodCreationFormState: StateFlow<FormStates.FoodCreation>
    val currentStep: StateFlow<Int>
    val formNameError: String?
    val formBrandError: String?
    val formAmountPerServingError: String?
    val formServingUnitError: String?
    val isBasicDataValid: Boolean
    val areBasicNutrientsValid: Boolean

    fun updateFoodCreationFormField(
        fieldName: FormFieldName.IFoodCreation,
        input: String
    )

    fun updateStep(
        step: Int,
        goesBack: Boolean = true,
        onExitForm: (() -> Unit)? = null,
        onSubmit: (() -> Unit)? = null
    )

    fun validateFoodNonBaseNutrients(nutrients: List<Nutrient>): Boolean

    fun resetFoodFormState()
}
