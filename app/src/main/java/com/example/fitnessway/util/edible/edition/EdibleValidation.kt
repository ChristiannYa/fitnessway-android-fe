package com.example.fitnessway.util.edible.edition

import com.example.fitnessway.constants.NutrientId
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.ServingUnit
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.BrandInlineRules
import com.example.fitnessway.util.form.field.InlineRules.FoodCreation.NameInlineRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.appBrandRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.nameRules
import com.example.fitnessway.util.form.field.Rules.FoodCreation.userBrandRules
import com.example.fitnessway.util.isValidEnum
import com.example.fitnessway.util.listEnumValues

fun getNameError(name: String): String? =
    if (name.isEmpty()) null
    else {
        val result = NameInlineRules(name.trim()) checkWith nameRules
        result.exceptionOrNull()?.message
    }

fun getBrandError(brand: String, source: EdibleSource): String? =
    if (brand.isEmpty()) null
    else {
        val result = BrandInlineRules(brand.trim()) checkWith when (source) {
            EdibleSource.APP -> appBrandRules
            EdibleSource.USER -> userBrandRules
        }
        result.exceptionOrNull()?.message
    }

fun getAmountPerServingError(amountPerServing: String): String? =
    validateDoubleAsString(amountPerServing, "Amount per serving")

fun getServingUnitError(servingUnit: String): String? =
    if (servingUnit.isEmpty()) null
    else {
        if (servingUnit.isValidEnum<ServingUnit>()) null
        else {
            "Must be one of ${listEnumValues<ServingUnit>()}"
        }
    }

fun isBaseChanged(
    currentForm: FormStates.FoodEdition,
    originalForm: FormStates.FoodEdition,
): Boolean {
    val nameChanged = currentForm.name != originalForm.name
    val brandChanged = currentForm.brand != originalForm.brand
    val amountPerServingChanged = currentForm.amountPerServing != originalForm.amountPerServing
    val servingUnitChanged = currentForm.servingUnit != originalForm.servingUnit
    return nameChanged || brandChanged || amountPerServingChanged || servingUnitChanged
}

fun isNutrientMapChanged(
    currentNutrients: Map<Int, String>,
    originalNutrients: Map<Int, String>,
    dvMap: Map<Int, String>
): Boolean {

    val anyNutrientChanged = currentNutrients.any { (id, value) -> originalNutrients[id] != value }
    val anyNutrientRemoved = originalNutrients.any { (id, _) -> !currentNutrients.contains(id) }

    return anyNutrientChanged || anyNutrientRemoved || dvMap.isNotEmpty()
}

fun isBaseError(form: FormStates.FoodEdition, source: EdibleSource): Boolean =
    with(form) {
        !(getNameError(this.name) == null &&
                getBrandError(this.brand, source) == null &&
                getAmountPerServingError(this.amountPerServing) == null &&
                getServingUnitError(this.servingUnit) == null)
    }

fun isBaseProvided(form: FormStates.FoodEdition, source: EdibleSource): Boolean =
    with(form) {
        val essentialsProvided = this.name.isNotEmpty() &&
                this.amountPerServing.isNotEmpty() &&
                this.servingUnit.isNotEmpty()

        val brandProvided = if (source == EdibleSource.APP) {
            this.brand.isNotEmpty()
        } else true

        essentialsProvided && brandProvided
    }

fun isNutrientListValid(
    currentNutrients: Map<Int, String>,
    originalNutrients: Map<Int, String>
): Boolean {
    val areAllGtZero = currentNutrients.values.all { with(it.toDoubleOrNull()) { this?.let { n -> n > 0.0 } ?: false } }
    val hasBasic = originalNutrients.keys.any { it in NutrientId.Base.ALL }
    return areAllGtZero && hasBasic
}

fun isBaseEditionValid(
    currentForm: FormStates.FoodEdition,
    originalForm: FormStates.FoodEdition,
    source: EdibleSource
): Boolean = isBaseChanged(currentForm, originalForm) &&
        !isBaseError(currentForm, source) &&
        isBaseProvided(currentForm, source)

fun isNutrientsEditionValid(
    currentNutrients: Map<Int, String>,
    originalNutrients: Map<Int, String>,
    dvMap: Map<Int, String>
): Boolean = isNutrientMapChanged(currentNutrients, originalNutrients, dvMap) &&
        isNutrientListValid(currentNutrients, originalNutrients)

fun isEdibleEditionValid(
    currentForm: FormStates.FoodEdition,
    originalForm: FormStates.FoodEdition,
    nutrientDvMap: Map<Int, String>,
    source: EdibleSource
): Boolean = isBaseEditionValid(currentForm, originalForm, source) &&
        isNutrientsEditionValid(currentForm.nutrients, originalForm.nutrients, nutrientDvMap)