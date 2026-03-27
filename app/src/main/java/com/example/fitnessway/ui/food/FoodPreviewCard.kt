package com.example.fitnessway.ui.food

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.ui.theme.AppModifiers.foodContainer
import com.example.fitnessway.util.UNutrient.getColor
import com.example.fitnessway.util.extensions.toTextAndPrecise

@Composable
fun FoodPreviewCard(
    preview: FoodPreview,
    onClick: ((Int) -> Unit)? = null,
) {
    val view = LocalView.current
    val hasBrand = !preview.base.brand.isNullOrBlank()

    Box(
        modifier = Modifier.foodContainer {
            onClick?.let {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                it(preview.id)
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f, false)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = preview.base.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (hasBrand) preview.base.brand else "~",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (preview.isUserPremium) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        preview.nutrientPreview.toList().forEach { nutrientPreview ->
                            val nutrientColor = getColor(nutrientPreview.color)

                            if (nutrientColor != null && nutrientPreview.amount != null) {
                                Text(
                                    text = nutrientPreview.amount.toTextAndPrecise(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = nutrientColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}