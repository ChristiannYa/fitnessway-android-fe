package com.example.fitnessway.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientPreferences
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.data.model.nutrient.Nutrient as NutrientM


object Nutrient {
    data class NutrientData(
        val intake: Double,
        val progress: Double,
        val remaining: Double,
        val over: Double,
        val isGoalMet: Boolean,
        val isOverGoal: Boolean
    )

    fun calcNutrientIntakeData(intakeData: NutrientAmountData): NutrientData {
        val intake = intakeData.amount
        val goal = intakeData.nutrientWithPreferences.preferences.goal ?: 0.0

        val progress = if (goal > 0) ((intake / goal) * 100) else 0.0

        val remaining = goal - intake
        val over = intake - goal

        return NutrientData(
            intake = intake,
            progress = progress,
            remaining = remaining,
            over = over,
            isGoalMet = remaining == 0.0,
            isOverGoal = remaining < 0
        )
    }

    fun <T> getAllNutrients(
        nutrients: NutrientsByType<T>
    ): List<T> {
        return nutrients.basic + nutrients.vitamin + nutrients.mineral
    }

    fun <T> filterNutrientsByType(
        nutrients: NutrientsByType<T>,
        type: NutrientType
    ): List<T> {
        return when (type) {
            NutrientType.BASIC -> nutrients.basic
            NutrientType.VITAMIN -> nutrients.vitamin
            NutrientType.MINERAL -> nutrients.mineral
        }
    }

    fun List<NutrientWithPreferences>.sortNutrientWithPreferencesByPremiumStatus(
        isPremiumUser: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isPremiumUser) {
            this.sortedBy { it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun List<NutrientM>.filterOutPremiumNutrients(
        isUserPremium: Boolean
    ): List<NutrientM> {
        return this.filter {
            (!it.isPremium || isUserPremium)
        }
    }

    fun getNutrientColor(hexColor: String?): Color? {
        return hexColor?.let {
            Color(it.toColorInt())
        }
    }

    fun formatNutrientsDataAsMap(
        nutrientsData: NutrientsByType<NutrientWithPreferences>,
        propertySelector: (NutrientPreferences) -> String
    ): Map<Int, String> {
        return getAllNutrients(nutrientsData).associate {
            val value = propertySelector(it.preferences)
            it.nutrient.id to value
        }
    }

    object Ui {
        @Composable
        fun NutrientCategoryTitle(
            type: NutrientType
        ) {
            val title = when (type) {
                NutrientType.BASIC -> "Nutrients"
                else -> "${
                    type.name
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                }s"
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        @Composable
        fun NutrientsBoxUi(
            nutrients: List<NutrientAmountData>,
            isDataMinimal: Boolean = false,
            /**
             * Represents the width of the **entire** nutrient UI (everything throughout
             * the goal, bar, and name)
             */
            contentWidth: Dp = 72.dp,

            /**
             * Represents the **individual** height of the nutrient amount progress.
             * Default result is obtained by: (72 * 0.8) * 2
             * - 72 is the value of the content width
             * - 0.8 comes from the progress bar content width taking a value of 0.8f
             * - * 2 because ideally we want the height to be double from the other nutrients
             */
            progressBarHeight: Dp = 115.2.dp,
        ) {
            val barShape = 16.dp

            nutrients.forEach { nutrientData ->
                val nutrient = nutrientData.nutrientWithPreferences.nutrient
                val nutrientType = nutrient.type
                val preferences = nutrientData.nutrientWithPreferences.preferences

                val nutrientColor = getNutrientColor(
                    preferences.hexColor
                ) ?: MaterialTheme.colorScheme.primary

                val calculatedNutrientData = calcNutrientIntakeData(intakeData = nutrientData)

                val spacedBy = if (nutrientType == NutrientType.BASIC) 12.dp else 8.dp

                val animatedProgress by animateFloatAsState(
                    targetValue = (calculatedNutrientData.progress / 100f).toFloat(),
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    ),
                    label = "intake_progress_animation"
                )

                Column(
                    modifier = Modifier.width(contentWidth),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = {
                        // Top part: Goal
                        if (!isDataMinimal) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                content = {
                                    Text(
                                        text = if (preferences.goal != null) {
                                            doubleFormatter(
                                                preferences.goal
                                            )
                                        } else "0",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontFamily = FontFamily.Default,
                                        color = nutrientColor
                                    )

                                    Text(
                                        text = nutrient.unit,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = nutrientColor.copy(0.5f)
                                    )
                                }
                            )
                        }

                        // Middle part: progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(progressBarHeight)
                                .clip(RoundedCornerShape(barShape))
                                .background(
                                    color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
                                    shape = RoundedCornerShape(barShape)
                                ),
                            content = {
                                val progressModifier = if (nutrientType == NutrientType.BASIC) {
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = -(spacedBy * 2))
                                } else Modifier.align(Alignment.Center)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(animatedProgress)
                                        .background(
                                            color = nutrientColor,
                                            shape = RoundedCornerShape(
                                                bottomStart = barShape,
                                                bottomEnd = barShape
                                            )
                                        )
                                        .align(Alignment.BottomCenter)
                                )

                                Text(
                                    text = doubleFormatter(nutrientData.amount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Default,
                                    color = WhiteFont,
                                    modifier = progressModifier
                                )
                            }
                        )

                        // Bottom part: nutrient name
                        val name = if (nutrient.type == NutrientType.VITAMIN
                            || nutrient.type == NutrientType.MINERAL
                        ) {
                            nutrient.symbol ?: nutrient.name
                        } else nutrient.name

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            content = {
                                if (!isDataMinimal) {
                                    Text(
                                        text = "${doubleFormatter(calculatedNutrientData.progress)}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Default,
                                        color = nutrientColor.copy(0.5f)
                                    )
                                }

                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}