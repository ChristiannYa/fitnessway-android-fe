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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.model.MNutrient.Model.Nutrient
import com.example.fitnessway.data.model.m_26.NutrientBase
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientDataAmount
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.tappable
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.ClickableConfiguration
import com.example.fitnessway.util.Ui.InputUi
import com.example.fitnessway.util.Ui.LabelSize
import com.example.fitnessway.util.extensions.getIntakeCalculation
import com.example.fitnessway.util.extensions.toPrecisedString
import com.example.fitnessway.data.model.m_26.NutrientPreferences as NutrientPreferencesM26


object UNutrient {
    data class NutrientIntakeCalculation(
        val intake: Double,
        val progress: Double,
        val remaining: Double,
        val over: Double,
        val isGoalMet: Boolean,
        val isOverGoal: Boolean
    )

    // Look at the nutrients' schema from the database
    val NUTRIENT_IDS_WITH_DV = setOf(
        // Fiber
        5,

        // Vitamins
        9,  // Vitamin A
        10, // Vitamin B12
        11, // Vitamin C
        12, // Vitamin D

        // Minerals
        13, // Calcium
        14, // Iron
        15, // Magnesium
        16  // Potassium
    )

    fun percentDvToNutrientAmount(
        nutrientId: Int,
        amount: Double
    ): Double {
        return when (nutrientId) {

            // ========== Fiber ==========
            5 -> { // Fiber
                val dv = 28.0 // g
                dv * (amount / 100.0)
            }

            // ========== Vitamins ==========
            9 -> { // Vitamin A
                val dv = 900.0 // mcg
                dv * (amount / 100.0)
            }

            10 -> { // Vitamin B12
                val dv = 2.4 // mcg
                dv * (amount / 100.0)
            }

            11 -> { // Vitamin C
                val dv = 90.0 // mg
                dv * (amount / 100.0)
            }

            12 -> { // Vitamin D
                val dv = 20.0 // mcg
                dv * (amount / 100.0)
            }

            // ========== Minerals ==========
            13 -> { // Calcium
                val dv = 1300.0 // mg
                dv * (amount / 100.0)
            }

            14 -> { // Iron
                val dv = 18.0 // mg
                dv * (amount / 100.0)
            }

            15 -> { // Magnesium
                val dv = 420.0 // mg
                dv * (amount / 100.0)
            }

            16 -> { // Potassium
                val dv = 4700.0 // mg
                dv * (amount / 100.0)
            }

            // ========== Not supported ==========
            else -> {
                throw IllegalArgumentException(
                    "Nutrient with id=$nutrientId does not support %DV conversion"
                )
            }
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
    fun getUserNutrientColor(color: String?, isUserPremium: Boolean): Color {
        return if (isUserPremium) {
            getColor(color) ?: MaterialTheme.colorScheme.primary
        } else MaterialTheme.colorScheme.primary
    }

    fun formatNutrientsDataAsMap(
        nutrientsData: com.example.fitnessway.data.model.m_26.NutrientsByType<NutrientData>,
        propertySelector: (NutrientPreferencesM26) -> String
    ): Map<Int, String> =
        nutrientsData
            .toList()
            .associate { it.base.id to propertySelector(it.preferences) }

    /**
     * `toReadable` formats the nutrient's type to be lowercase, and makes the first char uppercase.
     * `NutrientType.BASIC` has a more user-friendly format of "Nutrients"
     */
    fun NutrientType.toReadable(
        isPlural: Boolean = true,
        isLowercase: Boolean = false,
    ): String {
        val typeLowercased = when (this) {
            NutrientType.BASIC -> "nutrient"
            else -> this.name
        }.lowercase().replaceFirstChar {
            if (isLowercase) it.toString() else it.uppercase()
        }

        return typeLowercased + if (isPlural) "s" else ""
    }

    object Ui {
        @Composable
        fun buildAnnotatedString(
            nutrient: NutrientBase,
            isInDvMode: Boolean = false,
            nameColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
            informationColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
        ): AnnotatedString {
            return buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        color = nameColor
                    ),
                    block = { append("${nutrient.name} ") }
                )

                if (nutrient.type == NutrientType.MINERAL) {
                    withStyle(
                        style = SpanStyle(
                            color = informationColor
                        ),
                        block = { append(text = "(${nutrient.symbol}) ") }
                    )
                }

                withStyle(
                    style = SpanStyle(
                        color = informationColor
                    ),
                    block = {
                        append(if (isInDvMode) "(%DV)" else nutrient.unit.toString().lowercase())
                    }
                )
            }
        }

        @Composable
        fun NutrientLabelsFlowRow(
            nutrients: List<NutrientBase>,
            getColor: ((NutrientBase) -> Color)? = null,
            size: LabelSize = LabelSize.SMALL,
            textStyle: TextStyle = MaterialTheme.typography.labelMedium,
            clickableConfiguration: ClickableConfiguration<NutrientBase>? = null,
            labelModifier: (NutrientBase) -> Modifier = { Modifier },
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(size.paddingY)
            ) {
                nutrients.forEachIndexed { index, nutrient ->
                    val color = if (getColor != null) getColor(nutrient) else MaterialTheme.colorScheme.surfaceVariant

                    AppLabel(
                        text = UNutrient.Ui.buildAnnotatedString(
                            nutrient = nutrient,
                        ).text,
                        labelColor = color,
                        size = size,
                        textStyle = textStyle,
                        clickableConfiguration = clickableConfiguration,
                        data = nutrient,
                        modifier = labelModifier(nutrient)
                    )

                    if (index != nutrients.lastIndex) Spacer(modifier = Modifier.width(size.paddingX))
                }
            }
        }

        @Composable
        fun NutrientCategoryTitle(
            type: NutrientType,
            style: TextStyle = MaterialTheme.typography.titleSmall,
            modifier: Modifier = Modifier
        ) {
            Text(
                text = type.toReadable(),
                style = style,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
            )
        }

        @Composable
        fun NutrientFieldLabel(
            nutrient: NutrientBase,
            isFocused: Boolean,
            isInDvMode: Boolean = false
        ) {
            val inputOutlinedColors = InputUi.getOutlinedColors()

            val extraFieldContentColor = if (isFocused) {
                inputOutlinedColors.focusedLabelColor.copy(0.6f)
            } else inputOutlinedColors.unfocusedLabelColor.copy(0.6f)

            Text(
                text = UNutrient.Ui.buildAnnotatedString(
                    nutrient = nutrient,
                    isInDvMode = isInDvMode,
                    nameColor = if (isFocused) inputOutlinedColors.focusedLabelColor else {
                        inputOutlinedColors.unfocusedLabelColor
                    },
                    informationColor = extraFieldContentColor
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        @Composable
        fun GetChildrenToggleButton(
            isParent: Boolean = false,
            isChild: Boolean = false,
            onToggle: () -> Unit,
            modifier: Modifier = Modifier
        ) {
            return if (isParent || isChild) {
                Clickables.AppPngIconButton(
                    icon = Structure.AppIconSource.Vector(
                        if (isChild) {
                            Icons.Default.KeyboardDoubleArrowLeft
                        } else Icons.Default.KeyboardDoubleArrowRight
                    ),
                    contentDescription = "View ${if (isParent) "more" else "less"}",
                    onClick = onToggle,
                    iconTint = MaterialTheme.colorScheme.onBackground,
                    size = Clickables.AppIconButtonSize.XS,
                    modifier = modifier
                )
            } else Unit
        }

        @Composable
        fun NutrientsAsBox(
            nutrients: List<NutrientDataAmount>,
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

            onHasChildren: ((Int) -> Boolean)? = null,
            onViewAnalytics: ((Int) -> Unit)? = null,
            onToggleChildren: (Int) -> Unit,

            modifier: Modifier = Modifier
        ) {
            val barShape = 16.dp

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                nutrients.forEach { nutrientDataAmount ->
                    val preferences = nutrientDataAmount.data.preferences

                    val nutrientColor = getUserNutrientColor(
                        color = preferences.hexColor,
                        isUserPremium = isUserPremium
                    )

                    val calculatedNutrientData = nutrientDataAmount.getIntakeCalculation()

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
                        verticalArrangement = Arrangement.spacedBy(verticalSpace)
                    ) {
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
                                text = nutrientDataAmount.data.base.unit.name.lowercase(),
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
                                .then(
                                    onViewAnalytics?.let {
                                        Modifier.tappable { it(nutrientDataAmount.data.base.id) }
                                    } ?: Modifier
                                )
                        ) {

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
                                text = doubleFormatter(nutrientDataAmount.amount),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Default,
                                color = WhiteFont,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = if (nutrientDataAmount.data.base.type == NutrientType.BASIC) {
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = -(verticalSpace * 2))
                                } else Modifier.align(Alignment.Center)
                            )

                            GetChildrenToggleButton(
                                isParent = onHasChildren?.invoke(nutrientDataAmount.data.base.id) == true,
                                onToggle = { onToggleChildren(nutrientDataAmount.data.base.id) },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }

                        // Bottom part: nutrient name
                        val name = if (nutrientDataAmount.data.base.type != NutrientType.BASIC) {
                            nutrientDataAmount.data.base.symbol ?: nutrientDataAmount.data.base.name
                        } else nutrientDataAmount.data.base.name

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
                }
            }
        }

        @Composable
        fun NutrientsAsCircle(
            nutrients: List<NutrientDataAmount>,
            isUserPremium: Boolean,
            onHasChildren: ((Int) -> Boolean)? = null,
            onToggleChildren: ((Int) -> Unit)? = null
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                nutrients.forEach { nutrientInFood ->
                    val preferences = nutrientInFood.data.preferences

                    val nutrientColor = getUserNutrientColor(
                        color = preferences.hexColor,
                        isUserPremium = isUserPremium
                    )

                    val targetProgress = if (preferences.goal != null) {
                        (nutrientInFood.amount / preferences.goal)
                    } else 0.0

                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress.toFloat(),
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    )

                    val contentWidth = 70.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.requiredWidth(contentWidth)
                    ) {
                        Box(
                            modifier = Modifier.size(contentWidth)
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                color = nutrientColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 5.dp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                            )

                            Text(
                                text = doubleFormatter(nutrientInFood.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontFamily.Default,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            GetChildrenToggleButton(
                                isParent = onHasChildren?.invoke(nutrientInFood.data.base.id) == true,
                                onToggle = { onToggleChildren?.invoke(nutrientInFood.data.base.id) },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(y = 16.dp)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = preferences.getGoalRatioText(nutrientInFood.amount),
                                style = MaterialTheme.typography.labelLarge,
                                color = nutrientColor.copy(0.8f),
                                fontFamily = FontFamily.Default,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = nutrientInFood.data.base.name,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun NutrientsAsLine(
            nutrients: List<NutrientDataAmount>
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                nutrients.forEach { nutrientInFood ->
                    val preferences = nutrientInFood.data.preferences

                    val targetProgress = if (preferences.goal != null) {
                        ((nutrientInFood.amount / preferences.goal).toFloat())
                    } else 0f


                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    )

                    val color = if (preferences.hexColor != null) {
                        Color(preferences.hexColor.toColorInt())
                    } else MaterialTheme.colorScheme.primary

                    Column(
                        // Space between the nutrient left-right pair and bar
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            // Left side: nutrient name and symbol
                            if (nutrientInFood.data.byType == NutrientType.MINERAL) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = nutrientInFood.data.base.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    nutrientInFood.data.base.symbol?.let {
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
                                    text = nutrientInFood.data.base.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = doubleFormatter(nutrientInFood.amount, 2),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                )
                                Text(
                                    text = nutrientInFood.data.base.unit.name.lowercase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
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
                                    // text = "${doubleFormatter(percentage.toDouble())}%",
                                    text = preferences.getGoalRatioText(nutrientInFood.amount),
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
    }

    object Mock {
        object Nutrient {
            object Basic {
                val carbs = Nutrient(
                    id = 2,
                    name = "Carbs",
                    symbol = "",
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                )

                val protein = Nutrient(
                    id = 6,
                    name = "Protein",
                    symbol = "",
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = false
                )

                val fiber = Nutrient(
                    id = 5,
                    name = "Fiber",
                    symbol = "",
                    unit = "g",
                    type = NutrientType.BASIC,
                    isPremium = true
                )
            }

            object Vitamin {
                val vitaminA = Nutrient(
                    id = 9,
                    name = "A",
                    symbol = "A",
                    unit = "mcg",
                    type = NutrientType.VITAMIN,
                    isPremium = true
                )

                val vitaminC = Nutrient(
                    id = 11,
                    name = "C",
                    symbol = "C",
                    unit = "mg",
                    type = NutrientType.VITAMIN,
                    isPremium = false
                )

                val vitaminD = Nutrient(
                    id = 12,
                    name = "D",
                    symbol = "D",
                    unit = "mcg",
                    type = NutrientType.VITAMIN,
                    isPremium = false
                )
            }

            object Mineral {
                val calcium = Nutrient(
                    id = 13,
                    name = "Calcium",
                    symbol = "Ca",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = true
                )

                val iron = Nutrient(
                    id = 14,
                    name = "Iron",
                    symbol = "Fe",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = false
                )

                val potassium = Nutrient(
                    id = 16,
                    name = "Potassium",
                    symbol = "K",
                    unit = "mg",
                    type = NutrientType.MINERAL,
                    isPremium = false
                )
            }
        }
    }
}

fun NutrientPreferencesM26.getGoalRatioText(amount: Double) =
    this.goal
        ?.let { (amount / it) * 100 }
        ?.let { "${it.toPrecisedString()}%" }
        ?: "N/A"