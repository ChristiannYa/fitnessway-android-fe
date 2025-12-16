package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientPreferences
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.Nutrient.getAllNutrients
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GoalsManager : IGoalsManager {
    private lateinit var scope: CoroutineScope

    private val _goalsEditionFormState =
        MutableStateFlow<FormState<FormStates.NutrientGoals>?>(null)
    override val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?> =
        _goalsEditionFormState

    private val _originalGoalValues = MutableStateFlow<Map<Int, String>?>(null)

    private val _modifiedGoals = MutableStateFlow<Map<Int, String>>(emptyMap())
    override val modifiedGoals: StateFlow<Map<Int, String>> = _modifiedGoals

    private val _colorsEditionFormState =
        MutableStateFlow<FormState<FormStates.NutrientColors>?>(null)
    override val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?> =
        _colorsEditionFormState

    // @NOTE:
    //  `isGoalsFormValid` is a StateFlow because if it were a regular Boolean property,
    //  the ProfileGoalsScreen composable would not automatically recompose when the
    //  underlying form state changes.
    //  By using `StateFlow` + `combine` + `stateIn`, its value gets recalculated
    //  automatically whenever `_goalsEditionFormState` or `_originalGoalValues` change,
    //  and the UI observes these changes via `collectAsState()`.
    override val isGoalsFormValid: StateFlow<Boolean> by lazy {
        combine(
            _goalsEditionFormState,
            _originalGoalValues
        ) { formState, originalGoals ->
            formState?.let {
                val formGoals = it.data.goals

                val hasChanges = formGoals.any { (id, value) ->
                    originalGoals?.get(id) != value
                }

                val validationErrors = formGoals.mapNotNull { (id, goal) ->
                    val originalValue = originalGoals?.get(id)

                    // Error when original was not "~" and current is "~"
                    if (originalValue != null && originalValue != "~" && goal == "~") {
                        return@mapNotNull "Nutrient (id: $id) goal cannot be cleared"
                    }

                    // Error when current value is empty (but not "~")
                    if (goal.isEmpty()) {
                        return@mapNotNull "Nutrient (id: $id) goal cannot be empty"
                    }

                    // Skip validation for default "~"
                    if (goal == "~") {
                        null
                    } else {
                        validateDoubleAsString(
                            doubleAsString = goal,
                            itemToBeValidated = "nutrient (id: $id) goal"
                        )
                    }
                }

                hasChanges && validationErrors.isEmpty()
            } ?: false

        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    override fun initNutrientGoalsForm(
        nutrientsData: NutrientsByType<NutrientWithPreferences>
    ) {
        val goals = formatNutrientsDataAsMap(
            nutrientsData = nutrientsData,
            propertySelector = {
                if (it.goal != null) {
                    doubleFormatter(it.goal, 2)
                } else "~"
            }
        )

        _goalsEditionFormState.value = FormState(FormStates.NutrientGoals(goals))
        _originalGoalValues.value = goals
    }

    override fun updateGoalEditionFormField(
        fieldName: FormFieldName.NutrientGoalData,
        input: String
    ) {
        _goalsEditionFormState.value?.let { state ->
            val updatedValues = run {
                val goals = state.data.goals.toMutableMap()
                goals[fieldName.nutrientData.nutrient.id] = input
                state.data.copy(goals = goals)
            }

            _goalsEditionFormState.value = state.copy(data = updatedValues)
        }
    }

    override fun setGoalsThatChanged() {
        val goalsEditionFormState = _goalsEditionFormState.value
        val originalGoalValues = _originalGoalValues.value

        if (goalsEditionFormState == null || originalGoalValues == null) return

        _modifiedGoals.value = goalsEditionFormState.data.goals.filter {
            it.value != originalGoalValues[it.key]
        }
    }

    override fun initNutrientColorsForm(
        nutrientsData: NutrientsByType<NutrientWithPreferences>
    ) {
        val colors = formatNutrientsDataAsMap(
            nutrientsData = nutrientsData,
            propertySelector = { it.hexColor ?: "" }
        )

        _colorsEditionFormState.value = FormState(FormStates.NutrientColors(colors))
    }

    override fun updateColorsEditionFormField(
        fieldName: FormFieldName.NutrientColorUpdate,
        input: String
    ) {
        _colorsEditionFormState.value?.let { formState ->
            val updatedValues = run {
                val colors = formState.data.colors.toMutableMap()
                colors[fieldName.nutrientData.nutrient.id] = input
                formState.data.copy(colors = colors)
            }

            _colorsEditionFormState.value = formState.copy(data = updatedValues)
        }
    }

    override fun init(scope: CoroutineScope) {
        this.scope = scope
    }
}

private fun formatNutrientsDataAsMap(
    nutrientsData: NutrientsByType<NutrientWithPreferences>,
    propertySelector: (NutrientPreferences) -> String
): Map<Int, String> {
    return getAllNutrients(nutrientsData).associate {
        val value = propertySelector(it.preferences)
        it.nutrient.id to value
    }
}