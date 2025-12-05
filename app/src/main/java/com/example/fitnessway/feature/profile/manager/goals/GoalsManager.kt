package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.getAllNutrients
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoalsManager : IGoalsManager {
    private val _goalsEditionState = MutableStateFlow<FormState<FormStates.NutrientGoals>?>(null)
    override val goalsEditionState: StateFlow<FormState<FormStates.NutrientGoals>?> =
        _goalsEditionState

    private val _modifiedGoals = MutableStateFlow<List<Int>>(emptyList())
    override val modifiedGoals: StateFlow<List<Int>> = _modifiedGoals

    override fun initNutrientGoalsForm(
        goalsData: NutrientsByType<NutrientAmountData>
    ) {
        val goals = (getAllNutrients(goalsData)).associate {
            it.nutrient.id to if (it.goal != null) {
                doubleFormatter(it.goal)
            } else "~"
        }

        _goalsEditionState.value = FormState(
            data = FormStates.NutrientGoals(goals)
        )
    }

    override fun updateGoalEditionFormField(
        fieldName: FormFieldName.NutrientGoalData,
        input: String
    ) {
        _goalsEditionState.value?.let { state ->
            val updatedValues = {
                val goals = state.data.goals.toMutableMap()

                goals[fieldName.nutrientData.nutrient.id] = input
                state.data.copy(goals = goals)
            }

            _goalsEditionState.value = state.copy(data = updatedValues())
        }
    }

    override fun startFormEdition() {
        _goalsEditionState.value = _goalsEditionState.value?.edit()
    }
}