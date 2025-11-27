package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodEditionDetailField
import com.example.fitnessway.data.model.form.FoodEditionNutrientField
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.ui.shared.ToggleEditButton
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerMedium

@Composable
fun EditionMode(
    foodDetailFields: List<FoodEditionDetailField>,
    nutrientFields: List<Triple<NutrientType, List<FoodEditionNutrientField>, String>>,
    enabled: Boolean,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    onRemoveNutrient: (nutrientId: Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomCenter,
        content = {
            Box(
                modifier = Modifier
                    .areaContainerLarge(
                        areaColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        content = {
                            val backgroundColor = if (enabled) {
                                MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.surfaceVariant

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                content = {
                                    ToggleEditButton(
                                        text = "Done",
                                        onClick = onDone,
                                        enabled = enabled,
                                        backgroundColor = backgroundColor
                                    )

                                    ToggleEditButton(
                                        text = "Cancel",
                                        onClick = onCancel,
                                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                content = {
                                    fieldSection(
                                        title = "Details",
                                        fields = foodDetailFields,
                                        content = { EditableField(it) }
                                    )

                                    nutrientFields.forEach { (_, fields, title) ->
                                        fieldSection(
                                            title = title,
                                            fields = fields,
                                            content = {
                                                EditableField(
                                                    field = it,
                                                    onRemoveNutrient = onRemoveNutrient
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

private fun <T> LazyListScope.fieldSection(
    title: String,
    fields: List<T>,
    content: @Composable (T) -> Unit
) {
    if (fields.isNotEmpty()) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        width = 4.dp,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .areaContainerMedium(
                        areaColor = Color.Transparent
                    ),
                content = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            fields.forEach { content(it) }
                        }
                    )
                }
            )
        }
    }
}
