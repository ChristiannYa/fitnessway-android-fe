package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.form.FoodEditionDetailField
import com.example.fitnessway.data.model.form.FoodEditionNutrientField
import com.example.fitnessway.ui.theme.AppModifiers.areaContainerLarge

@Composable
fun EditionMode(
    foodDetailFields: List<FoodEditionDetailField>,
    foodSummaryFields: List<FoodEditionNutrientField>,
    foodVitaminFields: List<FoodEditionNutrientField>,
    foodMineralFields: List<FoodEditionNutrientField>,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            fieldSection(
                title = "Details",
                foodDetailFields,
                content = { EditableField(it) }
            )
            fieldSection(
                title = "Summary",
                foodSummaryFields,
                content = { EditableField(it) }
            )
            fieldSection(
                title = "Vitamins",
                foodVitaminFields,
                content = { EditableField(it) }
            )
            fieldSection(
                title = "Minerals",
                foodMineralFields,
                content = { EditableField(it) }
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
                modifier = Modifier.areaContainerLarge(),
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
