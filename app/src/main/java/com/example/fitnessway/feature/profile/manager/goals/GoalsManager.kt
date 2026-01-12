package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.validateDoubleAsString
import com.example.fitnessway.util.UNutrient.formatNutrientsDataAsMap
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GoalsManager() : IGoalsManager {
    private lateinit var scope: CoroutineScope

    private val _goalsEditionFormState =
        MutableStateFlow<FormState<FormStates.NutrientGoals>?>(null)
    override val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?> =
        _goalsEditionFormState

    private val _originalGoals = MutableStateFlow<Map<Int, String>?>(null)

    private val _modifiedGoals = MutableStateFlow<Map<Int, String>>(emptyMap())
    override val modifiedGoals: StateFlow<Map<Int, String>> = _modifiedGoals

    // @NOTE:
    //  `isGoalsFormValid` is a StateFlow because if it were a regular Boolean property,
    //  the ProfileGoalsScreen composable would not automatically recompose when the
    //  underlying form state changes.
    //  By using `StateFlow` + `combine` + `stateIn`, its value gets recalculated
    //  automatically whenever `_goalsEditionFormState` or `_originalGoals` change,
    //  and the UI observes these changes via `collectAsState()`.
    override val isGoalsFormValid: StateFlow<Boolean> by lazy {
        combine(
            _goalsEditionFormState,
            _originalGoals
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
                /*
                .also { errors ->
                logcat("validation errors: $errors")
            }

                 */

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
        _originalGoals.value = goals
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
        val originalGoalValues = _originalGoals.value

        if (goalsEditionFormState == null || originalGoalValues == null) return

        _modifiedGoals.value = goalsEditionFormState.data.goals.filter {
            it.value != originalGoalValues[it.key]
        }
    }

    fun init(scope: CoroutineScope) {
        this.scope = scope
    }
}