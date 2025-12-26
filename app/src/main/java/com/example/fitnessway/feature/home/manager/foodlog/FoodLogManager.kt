package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogCategories
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.roundIfClose
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.round

class FoodLogManager : IFoodLogManager {
    private val _foodLogCategory = MutableStateFlow<FoodLogCategories?>(null)
    override val foodLogCategory: StateFlow<FoodLogCategories?> = _foodLogCategory

    private val _selectedFoodLog = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLog: StateFlow<FoodLogData?> = _selectedFoodLog

    private val _selectedFoodToLog = MutableStateFlow<FoodInformation?>(null)
    override val selectedFoodToLog: StateFlow<FoodInformation?> = _selectedFoodToLog

    private val _selectedFoodLogToRemove = MutableStateFlow<FoodLogData?>(null)
    override val selectedFoodLogToRemove: StateFlow<FoodLogData?> = _selectedFoodLogToRemove

    private val _foodLogFormState = MutableStateFlow<FormState<FormStates.FoodLog>?>(null)
    override val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?> = _foodLogFormState

    private val _foodLogEditionFormState =
        MutableStateFlow<FormState<FormStates.FoodLogEdition>?>(null)
    override val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?> =
        _foodLogEditionFormState

    override val isFoodLogFormValid: Boolean
        get() = _foodLogFormState.value?.let {
            it.data.servings.isNotEmpty() && foodLogServingsDoubleError == null &&
                    it.data.amountPerServing.isNotEmpty() && foodLogAmPerSerDoubleError == null &&
                    it.data.time.isNotEmpty() && it.data.time.matches(Regex("^\\d{1,2}:\\d{2} [AP]M$"))
        } ?: false

    private val foodLogServingsDoubleError: String?
        get() = _foodLogFormState.value?.let { formState ->
            formState.data.servings.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Servings"
                )
            }
        }

    private val foodLogAmPerSerDoubleError: String?
        get() = _foodLogFormState.value?.let { formState ->
            formState.data.amountPerServing.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Amount Per Serving"
                )
            }
        }

    private val foodLogEditionServingsDoubleError: String?
        get() = _foodLogEditionFormState.value?.let { formState ->
            formState.data.servings.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Servings"
                )
            }
        }

    private val foodLogEditionFormAmPerSerDoubleError: String?
        get() = _foodLogEditionFormState.value?.let { formState ->
            formState.data.amountPerServing.let { value ->
                validateDoubleAsString(
                    doubleAsString = value,
                    itemToBeValidated = "Amount Per Serving"
                )
            }
        }

    override val isFoodLogEditionFormValid: Boolean
        get() = run {
            val formState = _foodLogEditionFormState.value
            val foodLog = _selectedFoodLog.value

            if (formState != null && foodLog != null) {
                val servingsPrecisedFormatted = doubleFormatter(
                    value = formState.data.servingsPrecised,
                    decimalPlaces = 4
                ) // formatted into the food's servings for propper comparison

                val foodServingsFormatted = doubleFormatter(
                    value = foodLog.servings,
                    decimalPlaces = 4
                )

                formState.data.servings.isNotEmpty() &&
                        foodLogEditionServingsDoubleError == null &&
                        formState.data.amountPerServing.isNotEmpty() &&
                        foodLogEditionFormAmPerSerDoubleError == null &&
                        servingsPrecisedFormatted != foodServingsFormatted
            } else false
        }

    override fun setFoodLogCategory(categories: FoodLogCategories) {
        _foodLogCategory.value = when (categories) {
            FoodLogCategories.BREAKFAST -> FoodLogCategories.BREAKFAST
            FoodLogCategories.LUNCH -> FoodLogCategories.LUNCH
            FoodLogCategories.DINNER -> FoodLogCategories.DINNER
            FoodLogCategories.SUPPLEMENT -> FoodLogCategories.SUPPLEMENT
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

    override fun initializeFoodLogForm(food: FoodInformation, time: String) {
        _foodLogFormState.value = FormState(
            data = FormStates.FoodLog(
                servings = doubleFormatter(1.0),
                amountPerServing = doubleFormatter(food.information.amountPerServing, 3),
                amountPerServingDb = food.information.amountPerServing,
                time = time
            )
        )
    }

    override fun initializeFoodLogEditionForm(foodLog: FoodLogData) {
        val amPerSerCalc = foodLog.servings * foodLog.food.information.amountPerServing
        val amPerSer = amPerSerCalc.roundIfClose()

        val formState = FormState(
            data = FormStates.FoodLogEdition(
                servings = doubleFormatter(foodLog.servings, 3),
                amountPerServing = doubleFormatter(amPerSer, 3),
                foodAmountPerServing = foodLog.food.information.amountPerServing,
                servingsPrecised = foodLog.servings
            )
        )

        _foodLogEditionFormState.value = formState
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
                        val amount = formState.data.foodAmountPerServing * newAmount
                        doubleFormatter(amount, 3)
                    } else formState.data.amountPerServing

                    val servingsPrecised = newAmount ?: formState.data.servingsPrecised

                    formState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing,
                        servingsPrecised = servingsPrecised
                    )
                }

                FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val (dynServings, servingsPrecised) = if (newAmount != null && newAmount > 0) {
                        val precised = newAmount / formState.data.foodAmountPerServing
                        val formatted = doubleFormatter(precised, 3)
                        formatted to precised
                    } else {
                        formState.data.servings to formState.data.servingsPrecised
                    }

                    formState.data.copy(
                        amountPerServing = input,
                        servings = dynServings,
                        servingsPrecised = servingsPrecised
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