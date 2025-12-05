package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
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

    override fun addGoalToModifiedList(nutrientId: Int) {
        val currentList = _modifiedGoals.value

        if (nutrientId !in currentList) {
            _modifiedGoals.value = currentList + nutrientId
        }
    }

    override fun removeGoalFromModifierList(nutrientId: Int) {
        TODO("Not yet implemented")
    }

    override fun startFormEdition() {
        _goalsEditionState.value = _goalsEditionState.value?.edit()
    }
}