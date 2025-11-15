package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FoodCreationBaseField
import com.example.fitnessway.data.model.form.FoodCreationNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.util.form.FormStates

class FoodCreationFieldsProvider(
    private val formState: FormStates.FoodCreation,
    private val onFieldUpdate: (FormFieldName.IFoodCreation, String) -> Unit
) {
    @Composable
    fun name(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.NAME,
            label = "Name",
            value = formState.name,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.NAME,
                    newValue
                )
            }
        )
    }

    @Composable
    fun brand(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.BRAND,
            label = "Brand",
            value = formState.brand,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.BRAND,
                    newValue
                )
            }
        )
    }

    @Composable
    fun amountPerServing(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.amountPerServing,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.AMOUNT_PER_SERVING,
                    newValue
                )
            }
        )
    }

    @Composable
    fun servingUnit(): FoodCreationBaseField {
        return FoodCreationBaseField(
            name = FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
            label = "Serving unit",
            value = formState.servingUnit,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.BaseField.SERVING_UNIT,
                    newValue
                )
            }
        )
    }

    @Composable
    fun nutrient(nutrient: Nutrient): FoodCreationNutrientField {
        return FoodCreationNutrientField(
            name = FormFieldName.FoodCreation.NutrientField(nutrient),
            label = "${nutrient.name} (${nutrient.unit})",
            value = formState.nutrients[nutrient.id] ?: "",
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodCreation.NutrientField(nutrient),
                    newValue
                )
            }
        )
    }
}