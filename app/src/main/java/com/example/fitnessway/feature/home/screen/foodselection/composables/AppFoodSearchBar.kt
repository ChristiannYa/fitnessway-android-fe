package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure

@Composable
fun AppFoodSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Structure.AppIconDynamic(
                source = Structure.AppIconSource.Vector(Icons.Default.Search),
                contentDescription = "Search app food"
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                Clickables.AppPngIconButton(
                    icon = Structure.AppIconSource.Vector(Icons.Default.Close),
                    contentDescription = "Close search bar",
                    size = Clickables.AppIconButtonSize.SMALL,
                    onClick = {
                        onQueryChange("")
                        focusManager.clearFocus()
                    }
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() }
        ),
        singleLine = true,
        shape = CircleShape,
        modifier = modifier.fillMaxWidth()
    )
}