package com.example.fitnessway.feature.profile.manager.colors

import com.example.fitnessway.data.model.form.FormFieldName
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.util.Nutrient.formatNutrientsDataAsMap
import com.example.fitnessway.util.form.FormState
import com.example.fitnessway.util.form.FormStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ColorsManager : IColorsManager {
    private lateinit var scope: CoroutineScope

    private val _colorsEditionFormState =
        MutableStateFlow<FormState<FormStates.NutrientColors>?>(null)
    override val colorsEditionFormState: StateFlow<FormState<FormStates.NutrientColors>?> =
        _colorsEditionFormState

    private val _originalColorValues = MutableStateFlow<Map<Int, String>?>(null)

    private val _modifiedColors = MutableStateFlow<Map<Int, String>>(emptyMap())
    override val modifiedColors: StateFlow<Map<Int, String>> = _modifiedColors

    override val isColorsFormValid: StateFlow<Boolean> by lazy {
        combine(
            _colorsEditionFormState,
            _originalColorValues
        ) { formState, originalColors ->
            formState?.let {
                val formColors = it.data.colors

                val hasChanges = formColors.any { (id, value) ->
                    originalColors?.get(id) != value
                }

                val validationErrors = formColors.mapNotNull { (id, color) ->
                    val originalValue = originalColors?.get(id)

                    // Skip validation for empty values
                    if (color.isEmpty()) return@mapNotNull null

                    // Only validate if the value has changed
                    if (originalValue != color) {
                        val isColorValid = color.matches(Regex("^[0-9A-Fa-f]{6}$"))

                        if (!isColorValid) {
                            return@mapNotNull "Color (id: $id) must be in #RRGGBB format"
                        }
                    }

                    null
                }

                hasChanges && validationErrors.isEmpty()
            } ?: false

        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    override fun initNutrientColorsForm(
        nutrientsData: NutrientsByType<NutrientWithPreferences>
    ) {
        val colors = formatNutrientsDataAsMap(
            nutrientsData = nutrientsData,
            propertySelector = { it.hexColor ?: "" }
        )

        _colorsEditionFormState.value = FormState(FormStates.NutrientColors(colors))
        _originalColorValues.value = colors
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

    override fun setColorsThatChanged() {
        val colorsEditionFormState = _colorsEditionFormState.value
        val originalColorValues = _originalColorValues.value

        if (colorsEditionFormState == null || originalColorValues == null) return

        _modifiedColors.value = colorsEditionFormState.data.colors.filter {
            it.value != originalColorValues[it.key]
        }
    }

    fun init(scope: CoroutineScope) {
        this.scope = scope
    }
}