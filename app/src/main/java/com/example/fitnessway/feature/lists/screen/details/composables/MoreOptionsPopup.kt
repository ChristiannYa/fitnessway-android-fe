package com.example.fitnessway.feature.lists.screen.details.composables

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation.popUpEnter
import com.example.fitnessway.util.Animation.popupExit

@Composable
fun MoreOptionsPopup(
    isVisible: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = popUpEnter,
        exit = popupExit,
        modifier = modifier,
        content = {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxSize(),
                content = {
                    val shape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .clip(shape)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = shape
                            )
                            .padding(20.dp)
                            .width(100.dp),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    16.dp
                                ),
                                content = {
                                    MoreOptionsButton(
                                        onClick = onEdit,
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        text = "Edit"
                                    )

                                    MoreOptionsButton(
                                        onClick = onDelete,
                                        backgroundColor = MaterialTheme.colorScheme.surfaceTint,
                                        text = "Delete"
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun MoreOptionsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    text: String
) {
    val shape = RoundedCornerShape(10.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .clickable(
                onClick = onClick
            )
            .padding(12.dp),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = WhiteFont,
                fontFamily = robotoSerifFamily
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MoreOptionsPopupPreview() {
    FitnesswayTheme {
        MoreOptionsPopup(
            isVisible = true,
            onEdit = {},
            onDelete = {}
        )
    }
}