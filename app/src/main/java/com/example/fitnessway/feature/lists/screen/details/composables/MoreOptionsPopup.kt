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
                    val shape = 16.dp
                    val space = 5.dp

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(shape))
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(shape)
                            )
                            .padding(space)
                            .width(100.dp),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(space),
                                content = {
                                    val topButtonShape = RoundedCornerShape(
                                        topStart = shape - 2.dp,
                                        topEnd = shape - 2.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp,
                                    )

                                    val bottomButtonShape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = shape - 2.dp,
                                        bottomEnd = shape - 2.dp,
                                    )

                                    MoreOptionsButton(
                                        onClick = onEdit,
                                        text = "Edit",
                                        modifier = Modifier
                                            .clip(shape = topButtonShape)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = topButtonShape
                                            )
                                    )

                                    MoreOptionsButton(
                                        onClick = onDelete,
                                        text = "Delete",
                                        modifier = Modifier
                                            .clip(shape = bottomButtonShape)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceTint,
                                                shape = bottomButtonShape
                                            )
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
    text: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
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