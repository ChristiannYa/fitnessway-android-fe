package com.example.fitnessway.feature.home.manager.foodlog

import com.example.fitnessway.data.model.m_26.EdibleInformation
import com.example.fitnessway.data.model.m_26.FoodInformationWithId
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.roundIfClose
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.extensions.calcAmountPerServing
import com.example.fitnessway.util.extensions.toPrecisedString
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.FormFieldName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodLogManager : IFoodLogManager {
    private val _foodLogCategory = MutableStateFlow<FoodLogCategory?>(null)
    override val foodLogCategory: StateFlow<FoodLogCategory?> = _foodLogCategory

    private val _foodList = MutableStateFlow<FoodLogListFilter?>(null)
    override val foodList: StateFlow<FoodLogListFilter?> = _foodList

    private val _selectedFoodLog = MutableStateFlow<FoodLog?>(null)
    override val selectedFoodLog: StateFlow<FoodLog?> = _selectedFoodLog

    private val _searchCriteria = MutableStateFlow<FoodToLogSearchCriteria?>(null)
    override val searchCriteria: StateFlow<FoodToLogSearchCriteria?> = _searchCriteria

    private val _foodToLog = MutableStateFlow<FoodInformationWithId?>(null)
    override val foodToLog: StateFlow<FoodInformationWithId?> = _foodToLog

    private val _selectedFoodLogToRemove = MutableStateFlow<FoodLog?>(null)
    override val selectedFoodLogToRemove: StateFlow<FoodLog?> = _selectedFoodLogToRemove

    private val _foodLogFormState = MutableStateFlow<FormState<FormStates.FoodLog>?>(null)
    override val foodLogFormState: StateFlow<FormState<FormStates.FoodLog>?> = _foodLogFormState

    private val _foodLogEditionFormState = MutableStateFlow<FormState<FormStates.FoodLogEdition>?>(null)
    override val foodLogEditionFormState: StateFlow<FormState<FormStates.FoodLogEdition>?> = _foodLogEditionFormState

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

    override fun setFoodLogCategory(category: FoodLogCategory) {
        _foodLogCategory.value = category
    }

    override fun setFoodList(list: FoodLogListFilter) {
        _foodList.value = list
    }

    override fun setSelectedFoodLog(foodLog: FoodLog) {
        _selectedFoodLog.value = foodLog
    }

    override fun setSearchCriteria(criteria: FoodToLogSearchCriteria) {
        _searchCriteria.value = criteria
    }

    override fun setFoodToLog(foodToLog: FoodInformationWithId) {
        _foodToLog.value = foodToLog
    }

    override fun setSelectedFoodLogToRemove(foodLog: FoodLog) {
        _selectedFoodLogToRemove.value = foodLog
    }

    override fun initializeFoodLogForm(food: EdibleInformation, time: String) {
        val amountPerServingDb = food.base.calcAmountPerServing()

        _foodLogFormState.value = FormState(
            data = FormStates.FoodLog(
                servings = (1.0).toPrecisedString(),
                amountPerServing = amountPerServingDb.toPrecisedString(3),
                amountPerServingDb = amountPerServingDb,
                time = time
            )
        )
    }

    override fun initializeFoodLogEditionForm(foodLog: FoodLog) {
        val foodAmountPerServing = foodLog.edibleInformation.base.calcAmountPerServing()
        val amountPerServingCalc = foodLog.servings * foodAmountPerServing
        val amountPerServing = amountPerServingCalc.roundIfClose(0.03)

        val formState = FormState(
            data = FormStates.FoodLogEdition(
                servings = foodLog.servings.toPrecisedString(3),
                amountPerServing = amountPerServing.toPrecisedString(3),
                foodAmountPerServing = foodLog.edibleInformation.base.amountPerServing,
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
            val foodLog = _selectedFoodLog.value ?: return
            val foodAmountPerServing = foodLog.edibleInformation.base.calcAmountPerServing()

            val updatedData = when (fieldName) {
                FormFieldName.FoodLogEdition.SERVINGS -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynAmountPerServing = if (newAmount != null && newAmount > 0) {
                        (foodAmountPerServing * newAmount).toPrecisedString(3)
                    } else formState.data.amountPerServing

                    val servingsPrecised = if (newAmount != null && newAmount > 0) {
                        newAmount
                    } else formState.data.servingsPrecised

                    formState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing,
                        servingsPrecised = servingsPrecised
                    )
                }

                FormFieldName.FoodLogEdition.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val (dynServings, servingsPrecised) = if (newAmount != null && newAmount > 0) {
                        val precised = newAmount / foodAmountPerServing

                        precised.toPrecisedString(3) to precised
                    } else formState.data.servings to formState.data.servingsPrecised


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
                        amount.toPrecisedString(3)
                    } else currentState.data.amountPerServing

                    currentState.data.copy(
                        servings = input,
                        amountPerServing = dynAmountPerServing
                    )
                }

                FormFieldName.FoodLog.AMOUNT_PER_SERVING -> {
                    val newAmount = input.toDoubleOrNull()

                    val dynServings = if (newAmount != null && newAmount > 0) {
                        val amount = newAmount / currentState.data.amountPerServingDb
                        amount.toPrecisedString()
                    } else currentState.data.servings

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
}