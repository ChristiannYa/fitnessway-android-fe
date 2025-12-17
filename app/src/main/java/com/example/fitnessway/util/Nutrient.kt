package com.example.fitnessway.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientPreferences
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.data.model.user.User
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

    fun <T> NutrientsByType<T>.mapNutrients(
        transform: (List<T>) -> List<T>
    ): NutrientsByType<T> {
        return NutrientsByType(
            basic = transform(basic),
            vitamin = transform(vitamin),
            mineral = transform(mineral)
        )
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

    fun getColor(color: String?): Color? {
        if (color.isNullOrEmpty()) return null

        return try {
            Color(color.toColorInt())
        } catch (_: IllegalArgumentException) {
            null
        }
    }


    @Composable
    fun getUserNutrientColor(color: String?, user: User): Color {
        return if (user.isPremium) {
            getColor(color) ?: MaterialTheme.colorScheme.primary
        } else MaterialTheme.colorScheme.primary
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
        fun NutrientsAsBox(
            nutrients: List<NutrientAmountData>,
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
            modifier: Modifier = Modifier
        ) {
            val barShape = 16.dp

            nutrients.forEach { nutrientData ->
                val nutrient = nutrientData.nutrientWithPreferences.nutrient
                val nutrientType = nutrient.type
                val preferences = nutrientData.nutrientWithPreferences.preferences

                val nutrientColor = getUserNutrientColor(
                    color = preferences.hexColor,
                    user = user
                )

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
                    modifier = modifier.width(contentWidth),
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
                                    color = MaterialTheme.colorScheme.surfaceVariant,
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

        @Composable
        fun NutrientsAsCircle(
            nutrients: List<NutrientAmountData>,
            user: User
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    nutrients.forEach { nutrientData ->
                        val nutrient = nutrientData.nutrientWithPreferences.nutrient
                        val preferences = nutrientData.nutrientWithPreferences.preferences

                        val nutrientColor = getUserNutrientColor(
                            color = preferences.hexColor,
                            user = user
                        )

                        val targetProgress =
                            if (preferences.goal != null) {
                                (nutrientData.amount / preferences.goal)
                            } else {
                                0.0
                            }

                        val animatedProgress by animateFloatAsState(
                            targetValue = targetProgress.toFloat(),
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        )

                        val percentage = if (preferences.goal != null) {
                            (nutrientData.amount / preferences.goal) * 100
                        } else {
                            0.0
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(68.dp),
                            ) {
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    color = nutrientColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round,
                                    strokeWidth = 5.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = doubleFormatter(nutrientData.amount),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily.Default
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = "${doubleFormatter(percentage)}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = nutrientColor.copy(0.8f),
                                    fontFamily = FontFamily.Default
                                )
                                Text(
                                    text = nutrient.name,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            )
        }

        @Composable
        fun NutrientsAsLine(
            nutrients: List<NutrientAmountData>
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                nutrients.forEach { nutrientData ->
                    val nutrient = nutrientData.nutrientWithPreferences.nutrient
                    val preferences = nutrientData.nutrientWithPreferences.preferences

                    val targetProgress = if (preferences.goal != null) {
                        ((nutrientData.amount / preferences.goal).toFloat())
                    } else {
                        0f
                    }

                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    )

                    val percentage = if (preferences.goal != null) {
                        ((nutrientData.amount / preferences.goal) * 100).toFloat()
                    } else {
                        0f
                    }

                    val color = if (preferences.hexColor != null) {
                        Color(preferences.hexColor.toColorInt())
                    } else {
                        MaterialTheme.colorScheme.primary
                    }

                    Column(
                        // Space between the nutrient left-right pair and bar
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            // Left side: nutrient name and symbol
                            if (nutrient.type == NutrientType.MINERAL) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = nutrient.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    nutrient.symbol?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                0.5f
                                            )
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = nutrient.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = doubleFormatter(
                                        nutrientData.amount
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                )
                                Text(
                                    text = nutrient.unit,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                        0.5f
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                LinearProgressIndicator(
                                    progress = { animatedProgress },
                                    color = color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round,
                                    modifier = Modifier
                                        .weight(1f),
                                )
                                Text(
                                    text = "${doubleFormatter(percentage.toDouble())}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                    color = color,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(68.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}