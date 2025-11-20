package com.example.fitnessway.util.form.field.provider

import androidx.compose.runtime.Composable
import com.example.fitnessway.data.model.form.FoodEditionDetailField
import com.example.fitnessway.data.model.form.FoodEditionNutrientField
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.util.form.FormStates

class FoodEditionFieldsProvider(
    private val formState: FormStates.FoodEdition,
    private val onFieldUpdate: (FormFieldName.IFoodEdition, String) -> Unit
) {
    @Composable
    fun name(): FoodEditionDetailField {
        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.NAME,
            label = "Name",
            value = formState.name,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.NAME,
                    newValue
                )
            }
        )
    }

    @Composable
    fun brand(): FoodEditionDetailField {
        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.BRAND,
            label = "Brand",
            value = formState.brand,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.BRAND,
                    newValue
                )
            }
        )
    }

    @Composable
    fun amountPerServing(): FoodEditionDetailField {
        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING,
            label = "Amount Per Serving",
            value = formState.amountPerServing,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.AMOUNT_PER_SERVING,
                    newValue
                )
            }
        )
    }

    @Composable
    fun servingUnit(): FoodEditionDetailField {
        return FoodEditionDetailField(
            name = FormFieldName.FoodEdition.DetailField.SERVING_UNIT,
            label = "Serving Unit",
            value = formState.servingUnit,
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.DetailField.SERVING_UNIT,
                    newValue
                )
            }
        )
    }

    @Composable
    fun nutrient(nutrient: Nutrient): FoodEditionNutrientField {
        return FoodEditionNutrientField(
            name = FormFieldName.FoodEdition.NutrientField(nutrient),
            label = "${nutrient.name} ${nutrient.unit}",
            value = formState.nutrients[nutrient.id] ?: "",
            updateState = { newValue ->
                onFieldUpdate(
                    FormFieldName.FoodEdition.NutrientField(nutrient),
                    newValue
                )
            }
        )
    }
}