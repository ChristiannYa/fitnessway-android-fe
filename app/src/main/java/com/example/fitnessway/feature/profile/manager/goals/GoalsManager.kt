package com.example.fitnessway.feature.profile.manager.goals

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.form.NutrientGoalEditionField
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Nutrient.filterNutrientsByType
import com.example.fitnessway.util.Nutrient.getAllNutrients
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.form.field.provider.NutrientGoalsFieldsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoalsManager : IGoalsManager {
    private val _goalsEditionFormState = MutableStateFlow<FormState<FormStates.NutrientGoals>?>(null)
    override val goalsEditionFormState: StateFlow<FormState<FormStates.NutrientGoals>?> =
        _goalsEditionFormState

    private val _nutrientFields = MutableStateFlow<Map<NutrientType, List<NutrientGoalEditionField>>?>(null)
    override val nutrientFields: StateFlow<Map<NutrientType, List<NutrientGoalEditionField>>?> = _nutrientFields

    private val _modifiedGoals = MutableStateFlow<List<Int>>(emptyList())
    override val modifiedGoals: StateFlow<List<Int>> = _modifiedGoals

    override fun initNutrientGoalsForm(
        goalsData: NutrientsByType<NutrientApiFormat>
    ) {
        val goals = (getAllNutrients(goalsData)).associate {
            it.nutrient.id to if (it.goal != null) {
                doubleFormatter(it.goal)
            } else "~"
        }

        val formState = FormState(
            data = FormStates.NutrientGoals(goals)
        )

        _goalsEditionFormState.value = formState

        computeNutrientFields(goalsData, formState)
    }

    private fun computeNutrientFields(
        goalsData: NutrientsByType<NutrientApiFormat>,
        formState: FormState<FormStates.NutrientGoals>
    ) {
        val fieldsProvider = NutrientGoalsFieldsProvider(
            formState = formState,
            onFieldUpdate = { fieldName, value ->
                updateGoalEditionFormField(fieldName, value)
            }
        )

        val fields = NutrientType.entries.associateWith { type ->
            val nutrientsByType = filterNutrientsByType(
                nutrients = goalsData,
                type = type
            )

            nutrientsByType.map { nutrientData ->
                fieldsProvider.nutrientGoal(nutrientData)
            }
        }

        _nutrientFields.value = fields
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
}