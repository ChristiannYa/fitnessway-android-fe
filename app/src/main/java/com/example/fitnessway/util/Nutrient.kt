package com.example.fitnessway.util

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientApiFormat
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.doubleFormatter
import kotlin.math.roundToInt

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
        val goal = intakeData.goal ?: 0.0

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

    fun List<NutrientApiFormat>.sortByPremiumStatus(
        isPremiumUser: Boolean
    ): List<NutrientApiFormat> {
        return if (!isPremiumUser) {
            this.sortedBy { it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun getNutrientColor(hexColor: String?): Color? {
        return hexColor?.let {
            Color(it.toColorInt())
        }
    }

    fun getNutrientsAsMap(
        nutrients: NutrientsByType<NutrientAmountData>
    ): Map<Int, String> {
        return (getAllNutrients(nutrients)).associate {
            it.nutrient.id to doubleFormatter(it.amount)
        }
        // Result: {1="10.5", 2="20.3", 3="15"}
        //
        // If `.map` where to be used instead it would result in:
        // [(1, "10.5"), (2, "20.3"), (3, "15")]
        // which is a `List` but we need a map
    }

    object Ui {
        @Composable
        fun NutrientsBoxUi(
            nutrients: NutrientsByType<NutrientAmountData>,
            nutrientType: NutrientType,
            user: User,
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
            val nutrientTypeName = nutrientType.name.lowercase().replaceFirstChar { it.uppercase() }

            val allowedNutrients = filterNutrientsByType(
                nutrients = nutrients,
                type = nutrientType
            ).filter {
                it.goal != null && (!it.nutrient.isPremium || user.isPremium)
            }

            if (allowedNutrients.isEmpty()) {
                Text(
                    text = "Set your $nutrientTypeName goals in order to see their intake progress",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                val barShape = 16.dp

                allowedNutrients.forEach { nutrient ->
                    val nutrientColor =
                        getNutrientColor(nutrient.nutrient.hexColor) ?: Color(0xFFFFFFFF)
                    val nutrientData = calcNutrientIntakeData(intakeData = nutrient)

                    val spacedBy = if (nutrientType == NutrientType.BASIC) 12.dp else 8.dp

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
                                            text = if (nutrient.goal != null) doubleFormatter(
                                                nutrient.goal
                                            ) else "0",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontFamily = FontFamily.Default,
                                            color = nutrientColor
                                        )

                                        Text(
                                            text = nutrient.nutrient.unit,
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
                                            .fillMaxHeight((nutrientData.progress / 100f).toFloat())
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
                                        text = doubleFormatter(nutrient.amount),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Default,
                                        color = WhiteFont,
                                        modifier = progressModifier
                                    )
                                }
                            )

                            // Bottom part: nutrient name
                            val name = if (nutrient.nutrient.type == NutrientType.VITAMIN
                                || nutrient.nutrient.type == NutrientType.MINERAL
                            ) {
                                nutrient.nutrient.symbol ?: nutrient.nutrient.name
                            } else nutrient.nutrient.name

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                content = {
                                    if (!isDataMinimal) {
                                        Text(
                                            text = "${nutrientData.progress.roundToInt()}%",
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
}