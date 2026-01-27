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
import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.MNutrient.Enum.NutrientType
import com.example.fitnessway.data.model.MNutrient.Model.Nutrient
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientWithPreferences
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.Ui.AppLabel
import com.example.fitnessway.util.Ui.ClickableConfiguration
import com.example.fitnessway.util.Ui.InputUi
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

    val Nutrient.hasDailyValue: Boolean get() = this.id in NUTRIENT_IDS_WITH_DV

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

    fun calcNutrientIntakeData(intakeData: NutrientDataWithAmount): NutrientData {
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

    fun <T> NutrientsByType<T>.filterNutrientsByType(type: NutrientType): List<T> {
        return when (type) {
            NutrientType.BASIC -> this.basic
            NutrientType.VITAMIN -> this.vitamin
            NutrientType.MINERAL -> this.mineral
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

    fun <T> NutrientsByType<T>.toTypedList(): List<Pair<NutrientType, List<T>>> {
        return listOf(
            NutrientType.BASIC to this.basic,
            NutrientType.VITAMIN to this.vitamin,
            NutrientType.MINERAL to this.mineral
        )
    }

    fun <T, R> buildNutrientsByType(
        nutrients: List<T>,
        transform: (
            type: NutrientType,
            nutrients: List<T>
        ) -> List<R>
    ): NutrientsByType<R> {
        return NutrientsByType(
            basic = transform(NutrientType.BASIC, nutrients),
            vitamin = transform(NutrientType.VITAMIN, nutrients),
            mineral = transform(NutrientType.MINERAL, nutrients)
        )
    }

    fun <T> buildNutrientsByType2(
        nutrients: List<T>,
        getType: (T) -> NutrientType
    ): NutrientsByType<T> {
        val grouped = nutrients.groupBy(getType)  // Single iteration

        return NutrientsByType(
            basic = grouped[NutrientType.BASIC] ?: emptyList(),
            vitamin = grouped[NutrientType.VITAMIN] ?: emptyList(),
            mineral = grouped[NutrientType.MINERAL] ?: emptyList()
        )
    }

    fun <T> NutrientsByType<T>.combine(): List<T> {
        return this.basic + this.vitamin + this.mineral
    }

    fun List<MNutrient.Model.NutrientWithPreferences>.findByNutrientId(
        nutrientId: Int
    ): MNutrient.Model.NutrientWithPreferences? {
        return this.find { it.nutrient.id == nutrientId }
    }

    fun List<NutrientWithPreferences>.sortPremiumNutrients(
        isPremiumUser: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isPremiumUser) this.sortedBy { it.nutrient.isPremium } else this
    }

    fun List<NutrientWithPreferences>.filterNonPremiumPreferences(
        isUserPremium: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isUserPremium) this.filter { !it.nutrient.isPremium } else this
    }

    fun List<NutrientDataWithAmount>.filterNonPremiumAmounts(
        isUserPremium: Boolean
    ): List<NutrientDataWithAmount> {
        return if (!isUserPremium) this.filter { !it.nutrientWithPreferences.nutrient.isPremium } else this
    }

    fun List<NutrientWithPreferences>.filterPremium(
        isUserPremium: Boolean
    ): List<NutrientWithPreferences> {
        return if (!isUserPremium) this.filter { it.nutrient.isPremium } else this
    }

    fun List<NutrientWithPreferences>.filterGoalSetPreferences(): List<NutrientWithPreferences> {
        return this.filter { it.preferences.goal != null }
    }

    fun List<NutrientDataWithAmount>.filterGoalSetAmounts(): List<NutrientDataWithAmount> {
        return this.filter { it.nutrientWithPreferences.preferences.goal != null }
    }

    fun List<NutrientWithPreferences>.filterGoalNotSet(): List<NutrientWithPreferences> {
        return this.filter { it.preferences.goal == null }
    }

    fun Int.getNutrientById(nutrients: List<Nutrient>): Nutrient? {
        return nutrients.find { it.id == this }
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
        return nutrientsData.combine().associate {
            val value = propertySelector(it.preferences)
            it.nutrient.id to value
        }
    }

    // @TODO: Replace usages with the `NutrientType.asTitle()` method
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

    /**
     * `toReadable` formats the nutrient's type to be lowercase, and makes the first char uppercase.
     * `NutrientType.BASIC` has a more user friendly format of "Nutrients"
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
            nutrient: MNutrient.Model.Nutrient,
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
                        append(if (isInDvMode) "(%DV)" else nutrient.unit)
                    }
                )
            }
        }

        @Composable
        fun NutrientLabelsFlowRow(
            nutrients: List<Nutrient>,
            getColor: ((Nutrient) -> Color)? = null,
            size: LabelSize = LabelSize.SMALL,
            textStyle: TextStyle = MaterialTheme.typography.labelMedium,
            clickableConfiguration: ClickableConfiguration<Nutrient>? = null,
            labelModifier: (Nutrient) -> Modifier = { Modifier },
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
                        color = color,
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
            nutrient: Nutrient,
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
        fun NutrientsAsBox(
            nutrients: List<NutrientDataWithAmount>,
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
            nutrients: List<NutrientDataWithAmount>,
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
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            )
        }

        @Composable
        fun NutrientsAsLine(
            nutrients: List<NutrientDataWithAmount>
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
                                    text = doubleFormatter(nutrientData.amount, 2),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Default,
                                )
                                Text(
                                    text = nutrient.unit,
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
            nutrients: List<NutrientDataWithAmount>,
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

    object Debug {
        fun Nutrient.logNutrientData() {
            val id = if (this.id < 10) "0${this.id}" else this.id

            val type = when (this.type) {
                NutrientType.BASIC -> "Ba"
                NutrientType.VITAMIN -> "Vi"
                NutrientType.MINERAL -> "Mi"
            }

            logcat("    [${id}]: ${if (this.isPremium) "✨" else "🆓"} $type ${this.name}")
        }

        fun NutrientDataWithAmount.logNutrientWithAmountData() {
            val nutrient = this.nutrientWithPreferences.nutrient
            logcat("    [${nutrient.id}] ${nutrient.name}: ${this.amount}")
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