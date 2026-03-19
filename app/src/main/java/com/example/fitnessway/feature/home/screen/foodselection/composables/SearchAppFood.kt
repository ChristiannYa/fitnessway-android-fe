package com.example.fitnessway.feature.home.screen.foodselection.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppFood(
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    modifier: Modifier = Modifier
) {
    var query = TextFieldState()
    var isSearching by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f }
                .clip(RoundedCornerShape(16.dp)),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query.text.toString(),
                    onQueryChange = {
                        query.edit {
                            replace(0, length, it)
                        }
                    },
                    onSearch = {
                        onSearch(query.text.toString())
                    },
                    expanded = isSearching,
                    onExpandedChange = { isSearching = it },
                    placeholder = { Text("Search") },
                    trailingIcon = {
                        if (isSearching) {
                            Clickables.AppPngIconButton(
                                icon = Structure.AppIconButtonSource.Vector(Icons.Default.Close),
                                contentDescription = "Close search bar",
                                size = Clickables.AppIconButtonSize.SMALL,
                                onClick = {
                                    isSearching = false
                                }
                            )
                        }
                    },
                    modifier = Modifier.padding(if (isSearching) 8.dp else 0.dp)
                )
            },
            expanded = isSearching,
            onExpandedChange = { isSearching = it },
            windowInsets = WindowInsets(top = 0.dp),
            colors = SearchBarDefaults.colors(
                containerColor = run {
                    if (isSearching) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    }
                },
                dividerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(count = searchResults.size) { index ->
                    val resultText = searchResults[index]

                    ListItem(
                        headlineContent = {
                            Text(resultText)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}