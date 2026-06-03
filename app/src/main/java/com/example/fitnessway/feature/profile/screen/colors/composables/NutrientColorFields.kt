package com.example.fitnessway.feature.profile.screen.colors.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.UNutrient.Ui.NutrientCategoryTitle
import com.example.fitnessway.util.UNutrient.getColor
import com.example.fitnessway.util.extensions.toHexCode
import com.example.fitnessway.util.form.field.FormFieldName
import com.example.fitnessway.util.form.field.NutrientColorUpdateField

@Composable
fun NutrientColorFields(
    fields: Map<NutrientType, List<NutrientColorUpdateField>>,
    onColorBoxTap: (FormFieldName.NutrientColorUpdate, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(scrollState)
    ) {
        fields.forEach { (nutrientType, fields) ->
            Box(modifier = Modifier.areaContainer()) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    NutrientCategoryTitle(nutrientType)

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        fields.forEach { field ->
                            key(field.name.nutrientData.base.id) {
                                NutrientColorField(field) { onColorBoxTap(field.name, it) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientColorField(
    field: NutrientColorUpdateField,
    onColorBoxTap: (String) -> Unit
) {
    val color = getColor("#" + field.value)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        NutrientColorUpdateFormField(
            field = field,
            modifier = Modifier.weight(1f)
        )

        val colorPreviewShape = RoundedCornerShape(5.dp)

        val primaryHex = MaterialTheme.colorScheme.primary.toHexCode()

        Box(
            modifier = Modifier
                .clip(colorPreviewShape)
                .size(21.dp)
                .border(
                    width = 1.dp,
                    color = if (color == null) {
                        MaterialTheme.colorScheme.primary
                    } else Color.Transparent,
                    shape = colorPreviewShape
                )
                .background(
                    color = color ?: Color.Transparent,
                    shape = colorPreviewShape
                )
                .clickable(onClick = { onColorBoxTap(field.value.ifBlank { primaryHex }) })
        )
    }
}