package com.example.fitnessway.feature.home.screen.foodselection.foodlog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily

private enum class EditionButtonTypes {
    EDIT,
    SAVE,
    CANCEL,
    SUBMIT
}

private val buttonRadius = 10.dp

@Composable
fun EditionButtons(
    isValid: Boolean? = false,
    isEditing: Boolean = false,
    isSubmitSuccess: Boolean? = false,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onSubmit: (() -> Unit)? = null,
    onSubmitText: String? = "Submit"
) {
    fun handleBtnPress(type: EditionButtonTypes) {
        when (type) {
            EditionButtonTypes.EDIT -> onEdit()
            EditionButtonTypes.SAVE -> if (isValid == true) onSave()
            EditionButtonTypes.CANCEL -> onCancel()
            EditionButtonTypes.SUBMIT -> onSubmit?.invoke()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        content = {
            if (onSubmit != null && isSubmitSuccess == true) {
                Box(
                    content = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Food logged",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )
            } else {
                if (isEditing) {
                    val saveTextColor = if (isValid == true) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.primary.copy(0.3f)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        content = {
                            EditionButton(
                                text = "Cancel",
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                onClick = { handleBtnPress(EditionButtonTypes.CANCEL) },
                                modifier = Modifier
                                    .weight(1f)
                            )
                            EditionButton(
                                text = "Save",
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                textColor = saveTextColor,
                                onClick = { handleBtnPress(EditionButtonTypes.SAVE) },
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    )
                } else {
                    Row(
                        horizontalArrangement = if (onSubmit != null) {
                            Arrangement.spacedBy(20.dp)
                        } else Arrangement.spacedBy(0.dp),
                        content = {
                            EditionButton(
                                text = "Edit",
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                textColor = MaterialTheme.colorScheme.primary,
                                onClick = { handleBtnPress(EditionButtonTypes.EDIT) },
                                modifier = Modifier.weight(1f)
                            )

                            if (onSubmit != null) {
                                EditionButton(
                                    text = onSubmitText ?: "Submit",
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    textColor = WhiteFont,
                                    onClick = { handleBtnPress(EditionButtonTypes.SUBMIT) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun EditionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(buttonRadius))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .fillMaxHeight()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(buttonRadius)
            ),
        content = {
            Text(
                text = text,
                fontFamily = robotoSerifFamily,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = textColor,
            )
        }
    )
}