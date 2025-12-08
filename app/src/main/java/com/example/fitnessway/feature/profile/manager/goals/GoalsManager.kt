package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter
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

    private val _modifiedGoals = MutableStateFlow<List<Int>>(emptyList())
    override val modifiedGoals: StateFlow<List<Int>> = _modifiedGoals

    override val isGoalsFormValid: StateFlow<Boolean> by lazy {
        combine(
            _goalsEditionFormState,
            _originalGoalValues
        ) { formState, originalGoals ->
            formState?.let {
                val formGoals = it.data.goals

                formGoals.any { (id, goal) ->
                    originalGoals?.get(id) != goal
                }

            } ?: false
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    override fun initNutrientGoalsForm(
        goalsData: NutrientsByType<NutrientApiFormat>
    ) {
        val goals = formatGoalsAsMap(goalsData)

        _goalsEditionFormState.value = FormState(
            data = FormStates.NutrientGoals(goals)
        )

        _originalGoalValues.value = goals
    }

    override fun updateGoalEditionFormField(
        fieldName: FormFieldName.NutrientGoalData,
        input: String
    ) {
        _goalsEditionFormState.value?.let { state ->
            val updatedValues = {
                val goals = state.data.goals.toMutableMap()

                goals[fieldName.nutrientData.nutrient.id] = input
                state.data.copy(goals = goals)
            }

            _goalsEditionFormState.value = state.copy(data = updatedValues())
        }
    }

    override fun startFormEdition() {
        _goalsEditionFormState.value = _goalsEditionFormState.value?.edit()
    }

    private fun formatGoalsAsMap(
        goalsData: NutrientsByType<NutrientApiFormat>
    ): Map<Int, String> {
        return getAllNutrients(goalsData).associate {
            it.nutrient.id to if (it.goal != null) {
                doubleFormatter(it.goal)
            } else "~"
        }
    }

    override fun init(scope: CoroutineScope) {
        this.scope = scope
    }
}