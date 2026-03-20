package com.example.fitnessway.util.food.creation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.form.field.FoodCreationBaseField

@Composable
fun SetBasicData(fields: List<FoodCreationBaseField>) {
    Surface(
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    fields.forEach { FoodCreationFormField(it) }
                }
            )
        }
    )
}