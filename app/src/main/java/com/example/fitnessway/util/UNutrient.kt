package com.example.fitnessway.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.model.nutrient.Nutrient
import com.example.fitnessway.data.model.nutrient.NutrientAmountData
import com.example.fitnessway.data.model.nutrient.NutrientPreferences
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.nutrient.NutrientWithPreferences
import com.example.fitnessway.data.model.nutrient.NutrientsByType
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.LabelSize


object UNutrient {
    enum class ScrollableNutrientsFormat {
        BOX,
        CIRCLE
    }

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

    fun <T> NutrientsByType<T>.combineAll(): List<T> {
        return this.basic + this.vitamin + this.mineral
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
        transform: (
            type: NutrientType,
            nutrients: List<T>
        ) -> List<T>
    ): NutrientsByType<T> {
        return NutrientsByType(
            basic = transform(NutrientType.BASIC, basic),
            vitamin = transform(NutrientType.VITAMIN, vitamin),
            mineral = transform(NutrientType.MINERAL, mineral)
        )
    }

    fun List<NutrientWithPreferences>.sortPremiumNutrients(
        isPremiumUser: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isPremiumUser) {
            this.sortedBy { it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun List<NutrientWithPreferences>.filterOutPremiumNutrients(
        isUserPremium: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isUserPremium) {
            this.filter { !it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun List<NutrientWithPreferences>.filterOutNonPremiumNutrients(
        isUserPremium: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isUserPremium) {
            this.filter { it.nutrient.isPremium }
        } else {
            this
        }
    }

    fun List<NutrientWithPreferences>.filterOutNutrientsWithoutGoal(): List<NutrientWithPreferences> {
        return this.filter { it.preferences.goal != null }
    }

    fun List<NutrientWithPreferences>.filterOutNutrientsWithGoal(): List<NutrientWithPreferences> {
        return this.filter { it.preferences.goal == null }
    }


    fun List<Nutrient>.getIds(): List<Int> = this.map { it.id }

    fun getColor(color: String?): Color? {
        if (color.isNullOrEmpty()) return null

        return try {
            Color(color.toColorInt())
        } catch (_: IllegalArgumentException) {
            null
        }
    }


    @Composable
    fun getUserNutrientColor(color: String?, isUserPremium: Boolean): Color {
        return if (isUserPremium) {
            getColor(color) ?: MaterialTheme.colorScheme.primary
        } else MaterialTheme.colorScheme.primary
    }

    fun formatNutrientsDataAsMap(
        nutrientsData: NutrientsByType<NutrientWithPreferences>,
        propertySelector: (NutrientPreferences) -> String
    ): Map<Int, String> {
        return nutrientsData.combineAll().associate {
            val value = propertySelector(it.preferences)
            it.nutrient.id to value
        }
    }

    fun getNutrientCategoryTitle(type: NutrientType): String {
        return when (type) {
            NutrientType.BASIC -> "Nutrients"
            else -> "${
                type.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }
            }s"
        }
    }

    object Ui {
        @Composable
        fun NutrientLabelsFlowRow(
            nutrients: List<Nutrient>,
            textStyle: TextStyle = MaterialTheme.typography.labelMedium,
            color: Color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                nutrients.forEach {
                    AppLabel(
                        text = it.name,
                        color = color,
                        size = LabelSize.SMALL,
                        textStyle = textStyle
                    )
                }
            }
        }

        @Composable
        fun NutrientCategoryTitle(
            type: NutrientType
        ) {
            val title = getNutrientCategoryTitle(type)

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        @Composable
        fun NutrientsAsBox(
            nutrients: List<NutrientAmountData>,
            isUserPremium: Boolean,
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

            /**
             * Represents the vertical space between the intake amount group, progress bar,
             * and nutrient name
             */
            verticalSpace: Dp = 12.dp,
            modifier: Modifier = Modifier
        ) {
            val barShape = 16.dp

            nutrients.forEach { nutrientData ->
                val nutrient = nutrientData.nutrientWithPreferences.nutrient
                val nutrientType = nutrient.type
                val preferences = nutrientData.nutrientWithPreferences.preferences

                val nutrientColor = getUserNutrientColor(
                    color = preferences.hexColor,
                    isUserPremium = isUserPremium
                )

                val calculatedNutrientData = calcNutrientIntakeData(intakeData = nutrientData)

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
                    verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Top part: Goal
                            if (!isDataMinimal) {
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
                            }

                            Text(
                                text = nutrient.unit,
                                style = MaterialTheme.typography.labelSmall,
                                color = nutrientColor.copy(0.5f)
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
                                )
                        ) {
                            val progressModifier = if (nutrientType == NutrientType.BASIC) {
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = -(verticalSpace * 2))
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = progressModifier
                            )
                        }

                        // Bottom part: nutrient name
                        val name = if (nutrient.type == NutrientType.VITAMIN
                            || nutrient.type == NutrientType.MINERAL
                        ) {
                            nutrient.symbol ?: nutrient.name
                        } else nutrient.name

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
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
                    }
                )
            }
        }

        @Composable
        fun NutrientsAsCircle(
            nutrients: List<NutrientAmountData>,
            isUserPremium: Boolean
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
                            isUserPremium = isUserPremium
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

                        val contentWidth = 70.dp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.requiredWidth(contentWidth)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(contentWidth)
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
                                    fontFamily = FontFamily.Default,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                                    fontFamily = FontFamily.Default,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                                    drawStopIndicator = { },
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "${doubleFormatter(percentage.toDouble())}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                    color = color,
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(68.dp),
                                )
                            }
                        )
                    }
                }
            }
        }

        @Composable
        fun PagedNutrients(
            nutrients: List<NutrientAmountData>,
            displayFormat: ScrollableNutrientsFormat,
            isDataMinimal: Boolean = false,
            isBasicNutrient: Boolean = true,
            isBaseSizeDisplay: Boolean = true,
            isUserPremium: Boolean,
            modifier: Modifier = Modifier
        ) {
            val chunkedNutrients = nutrients.chunked(4)
            val pagerState = rememberPagerState(pageCount = { chunkedNutrients.size })

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = modifier.fillMaxWidth()
                ) { page ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (displayFormat) {
                            ScrollableNutrientsFormat.BOX -> {
                                val contentWidth = if (isBaseSizeDisplay) 72.dp else 62.dp

                                val progressBarHeight = if (isBaseSizeDisplay) {
                                    if (isBasicNutrient) {
                                        115.2.dp
                                    } else (115.2 / 2).dp
                                } else {
                                    if (isBasicNutrient) {
                                        (contentWidth * 0.7f) * 2
                                    } else {
                                        contentWidth * 0.8f
                                    }
                                }

                                val verticalSpace = if (isBaseSizeDisplay) 10.dp else 12.dp


                                NutrientsAsBox(
                                    nutrients = chunkedNutrients[page],
                                    isDataMinimal = isDataMinimal,
                                    isUserPremium = isUserPremium,
                                    progressBarHeight = progressBarHeight,
                                    contentWidth = contentWidth,
                                    verticalSpace = verticalSpace
                                )
                            }

                            ScrollableNutrientsFormat.CIRCLE -> {
                                NutrientsAsCircle(
                                    nutrients = chunkedNutrients[page],
                                    isUserPremium = isUserPremium
                                )
                            }
                        }
                    }
                }

                if (chunkedNutrients.size > 1) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(chunkedNutrients.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}