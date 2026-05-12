package com.example.fitnessway.feature.home.screen.main.composables.nutrient_contribution

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer

@Composable
fun IntakeInformationWrapper(
    information: @Composable (TextStyle, FontWeight) -> Unit,
    withContainer: Boolean = true,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.labelMedium
    val fontWeight = FontWeight.SemiBold

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(
                if (withContainer) {
                    modifier
                        .areaContainer(
                            areaColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f),
                            shape = RoundedCornerShape(4.dp),
                            size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                            isContentHugged = true
                        )
                } else modifier
            )
    ) {
        information(textStyle, fontWeight)
    }
}