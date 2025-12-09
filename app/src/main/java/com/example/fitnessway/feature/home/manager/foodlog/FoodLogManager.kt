package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodLogManager : IFoodLogManager {
    private val _foodLogCategory = MutableStateFlow("")
    override val foodLogCategory: StateFlow<String> = _foodLogCategory

    private val _selectedFoodLog = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLog: StateFlow<FoodLogData?> = _selectedFoodLog

    private val _selectedFoodToLog = MutableStateFlow<FoodInformation?>(null)
    override val selectedFoodToLog: StateFlow<FoodInformation?> = _selectedFoodToLog

    private val _selectedFoodLogToRemove = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLogToRemove: StateFlow<FoodLogData?> = _selectedFoodLogToRemove

    private val _foodLogEditionFormState =
        MutableStateFlow<FormState<FormStates.FoodLogEdition>?>(null)
    override val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?> =
        _foodLogEditionFormState

    private val _foodLogFormState = MutableStateFlow<FormState<FormStates.FoodLog>?>(null)
    override val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?> = _foodLogFormState

    override val isFoodLogFormValid: Boolean
        get() = foodLogFormState.value?.data?.let { data ->
            val servings = data.servings.toDoubleOrNull()
            val amountPerServing = data.amountPerServing.toDoubleOrNull()
            val time = data.time

            servings != null && servings > 0 &&
                    amountPerServing != null &&
                    amountPerServing > 0 &&
                    time.isNotEmpty() && time.matches(Regex("^\\d{1,2}:\\d{2} [AP]M$"))
        } ?: false

    override val fleFormServsError: String?
        get() = _foodLogEditionFormState.value?.let { formState ->
            formState.data.servings.let { value -> validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Servings"
                )
            }
        }

    override val isFleFormValid: Boolean
        get() = _foodLogEditionFormState.value?.let {
            it.data.servings.isNotEmpty() && fleFormServsError == null &&
                    it.data.amountPerServing.isNotEmpty() && fleFormAmPerSerError == null
        } ?: false

    override val fleFormAmPerSerError: String?
        get() = _foodLogEditionFormState.value?.let { formState ->
            formState.data.amountPerServing.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Amount Per Serving"
                )
            }
        }

    override fun setFoodLogCategory(categories: FoodLogCategories) {
        _foodLogCategory.value = when (categories) {
            FoodLogCategories.BREAKFAST -> "breakfast"
            FoodLogCategories.LUNCH -> "lunch"
            FoodLogCategories.DINNER -> "dinner"
            FoodLogCategories.SUPPLEMENT -> "supplement"
        }
    }

    override fun setSelectedFoodLog(foodLog: FoodLogData) {
        _selectedFoodLog.value = foodLog
    }

    override fun setSelectedFoodToLog(food: FoodInformation) {
        _selectedFoodToLog.value = food
    }

    override fun setSelectedFoodLogToRemove(foodLog: FoodLogData) {
        _selectedFoodLogToRemove.value = foodLog
    }

    override fun initializeFoodLogEditionForm(foodLog: FoodLogData) {
        val amPerSer = foodLog.servings * foodLog.food.information.amountPerServing

        _foodLogEditionFormState.value = FormState(
            data = FormStates.FoodLogEdition(
                servings = doubleFormatter(foodLog.servings),
                amountPerServing = doubleFormatter(amPerSer),
                amountPerServingDb = foodLog.food.information.amountPerServing
            )
        )
    }

    override fun initializeFoodLogForm(food: FoodInformation, time: String) {
        _foodLogFormState.value = FormState(
            data = FormStates.FoodLog(
                servings = doubleFormatter(1.0),
                amountPerServing = doubleFormatter(food.information.amountPerServing),
                amountPerServingDb = food.information.amountPerServing,
                time = time
            )
        )
    }

    override fun updateFoodLogEditionFormField(
        fieldName: FormFieldName.FoodLogEdition,
        input: String
    ) {
        _foodLogEditionFormState.value?.let { formState ->
            val updatedData = when (fieldName) {
                FormFieldName.FoodLogEdition.SERVINGS -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynAmountPerServing = if (newAmount != null && newAmount > 0) {
                        val amount = formState.data.amountPerServingDb * newAmount

                        doubleFormatter(amount, 2)
                    } else formState.data.amountPerServing

                    formState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing
                    )
                }

                FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynServings = if (newAmount != null && newAmount > 0) {
                        val amount = newAmount / formState.data.amountPerServingDb
                        doubleFormatter(amount, 2)
                    } else formState.data.servings

                    formState.data.copy(
                        amountPerServing = input,
                        servings = dynServings
                    )
                }
            }

            _foodLogEditionFormState.value = formState.copy(data = updatedData)
        }
    }

    override fun updateFoodLogFormField(
        fieldName: FormFieldName.FoodLog,
        input: String
    ) {
        _foodLogFormState.value?.let { currentState ->
            val updatedData = when (fieldName) {
                FormFieldName.FoodLog.SERVINGS -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynAmountPerServing = if (newAmount != null && newAmount > 0) {
                        val amount = currentState.data.amountPerServingDb * newAmount
                        doubleFormatter(amount, 2)
                    } else currentState.data.amountPerServing // Keep current if invalid

                    currentState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing
                    )
                }

                FormFieldName.FoodLog.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynServings = if (newAmount != null && newAmount > 0) {
                        val amount = newAmount / currentState.data.amountPerServingDb
                        doubleFormatter(amount, 2)
                    } else currentState.data.servings // Keep current if invalid

                    currentState.data.copy(
                        amountPerServing = input,
                        servings = dynServings
                    )
                }

                FormFieldName.FoodLog.TIME -> {
                    currentState.data.copy(time = input)
                }
            }

            _foodLogFormState.value = currentState.copy(data = updatedData)
        }
    }

    override fun startFormEdit(formState: FormStates) {
        when (formState) {
            is FormStates.FoodLog -> _foodLogFormState.value = _foodLogFormState.value?.edit()
            is FormStates.FoodLogEdition -> _foodLogEditionFormState.value =
                _foodLogEditionFormState.value?.edit()

            else -> {}
        }
    }

    override fun cancelFormEdit(formState: FormStates) {
        when (formState) {
            is FormStates.FoodLog -> _foodLogFormState.value = _foodLogFormState.value?.cancel()
            is FormStates.FoodLogEdition -> _foodLogEditionFormState.value =
                _foodLogEditionFormState.value?.cancel()

            else -> {}
        }
    }

    override fun saveFormEdit(formState: FormStates) {
        when (formState) {
            is FormStates.FoodLog -> _foodLogFormState.value = _foodLogFormState.value?.save()

            is FormStates.FoodLogEdition -> _foodLogEditionFormState.value =
                _foodLogEditionFormState.value?.save()

            else -> {}
        }
    }
}