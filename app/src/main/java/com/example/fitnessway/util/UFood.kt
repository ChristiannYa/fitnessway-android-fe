package com.example.fitnessway.util

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toErrorMessageOrNull
import com.example.fitnessway.data.model.MFood.Enum.FoodLogCategories
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.model.MNutrient.Model.NutrientDataWithAmount
import com.example.fitnessway.data.model.MNutrient.Model.NutrientsByType
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.foodContainer
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UNutrient.Debug.logNutrientWithAmountData
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsLine
import com.example.fitnessway.util.UNutrient.Ui.PagedNutrients
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.getColor
import com.example.fitnessway.util.UNutrient.mapNutrients
import com.example.fitnessway.util.form.FormStates
import com.example.fitnessway.util.nutrient.INutrientDvControls

object UFood {
    enum class FoodNutrientIntakesOperation {
        ADD, SUBTRACT
    }

    fun calcNutrientIntakesFromFoodLog(
        currentIntakes: NutrientsByType<NutrientDataWithAmount>,
        foodLog: FoodLogData,
        operation: FoodNutrientIntakesOperation
    ): NutrientsByType<NutrientDataWithAmount> {
        return currentIntakes.mapNutrients { _, intakes ->
            intakes.map { intake ->
                val foodNutrientAmountData = foodLog.food.nutrients.combine().find {
                    val nutrient = it.nutrientWithPreferences.nutrient
                    nutrient.id == intake.nutrientWithPreferences.nutrient.id
                }

                if (foodNutrientAmountData != null) {
                    val newAmount = when (operation) {
                        FoodNutrientIntakesOperation.ADD -> intake.amount + foodNutrientAmountData.amount
                        FoodNutrientIntakesOperation.SUBTRACT -> intake.amount - foodNutrientAmountData.amount
                    }

                    intake.copy(amount = newAmount)
                } else intake
            }
        }
    }

    /**
     * Food log data is not being asked for because the function would not be compatible for
     * when editing a food **to be** logged, not an **existing** one, hence just the current and
     * new serving sizes are asked for instead
     */
    fun calcNutrientIntakesFromFoodLogServings(
        nutrients: NutrientsByType<NutrientDataWithAmount>,
        currentServings: Double,
        newServings: Double
    ): NutrientsByType<NutrientDataWithAmount> {
        return nutrients.mapNutrients { _, nutrientsList ->
            nutrientsList.map { nutrientData ->
                val foodNutrientAmountData = nutrients.combine().find {
                    val nutrient = it.nutrientWithPreferences.nutrient
                    nutrient.id == nutrientData.nutrientWithPreferences.nutrient.id
                }

                if (foodNutrientAmountData != null) {
                    val originalAmount = foodNutrientAmountData.amount / currentServings
                    val newAmount = (originalAmount) * newServings
                    nutrientData.copy(amount = newAmount)
                } else nutrientData
            }
        }
    }

    fun FoodLogsByCategory.mapFoodLogs(
        transform: (
            category: FoodLogCategories,
            logs: List<FoodLogData>
        ) -> List<FoodLogData>
    ): FoodLogsByCategory = FoodLogsByCategory(
        breakfast = transform(FoodLogCategories.BREAKFAST, breakfast),
        lunch = transform(FoodLogCategories.LUNCH, lunch),
        dinner = transform(FoodLogCategories.DINNER, dinner),
        supplement = transform(FoodLogCategories.SUPPLEMENT, supplement)
    )

    fun FoodLogsByCategory.combineAll(): List<FoodLogData> {
        return this.breakfast + this.lunch + this.dinner + this.supplement
    }

    /**
     * Returns a list of food log ids
     */
    fun List<FoodLogData>.getIds(): List<Int> = this.map { it.id }

    fun List<FoodInformation>.getFoodById(id: Int): FoodInformation? {
        return this.find { it.information.id == id }
    }

    object Ui {
        fun getFoodBrandText(brand: String?): String {
            return if (brand == null || brand.isEmpty()) {
                "~"
            } else brand
        }

        fun getFoodLogCategory(category: FoodLogCategories): String {
            return category.name.lowercase().replaceFirstChar { it.uppercase() }
        }

        @Composable
        fun getFoodBrandColor(): Color {
            return MaterialTheme.colorScheme.onBackground.copy(0.6f)
        }

        @Composable
        fun FoodPreview(
            food: FoodInformation,
            isUserPremium: Boolean = false,
            showsNutrientPreview: Boolean = false,
            onClick: (() -> Unit)? = null
        ) {
            val view = LocalView.current
            val missingBrand = food.information.brand.isNullOrEmpty()

            Box(
                modifier = Modifier.foodContainer {
                    onClick?.let {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        it()
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
                                text = food.information.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (missingBrand) "~" else food.information.brand,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isUserPremium && showsNutrientPreview) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                food.nutrients.basic.forEach { (nutrientData, amount) ->
                                    val nutrientColor = getColor(nutrientData.preferences.hexColor)

                                    if (nutrientColor != null) {
                                        Text(
                                            text = doubleFormatter(amount),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = nutrientColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (food.metadata.isFavorite) {
                        Structure.AppIconDynamic(
                            source = Structure.AppIconButtonSource.Vector(Icons.Default.Star),
                            contentDescription = "Food favorite indicator",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        @Composable
        fun UserFoodsList(
            state: UiState<List<FoodInformation>>,
            isUserPremium: Boolean = false,
            showsNutrientPreview: Boolean = false,
            onFoodClick: (FoodInformation) -> Unit,
            loadingVerticalSpace: Dp = 16.dp,
            modifier: Modifier = Modifier
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
            ) {
                when (state) {
                    is UiState.Loading -> item {
                        Column(verticalArrangement = Arrangement.spacedBy(loadingVerticalSpace)) {
                            repeat(12) {
                                Loading.Composable(height = 32.dp)
                            }
                        }
                    }

                    is UiState.Success -> {
                        val foods = state.data

                        if (foods.isEmpty()) {
                            item {
                                NotFoundMessage("Foods that you add to your list will appear here")
                            }
                        } else {
                            items(
                                items = foods,
                                key = { food -> food.information.id }
                            ) { food ->
                                FoodPreview(
                                    food = food,
                                    isUserPremium = isUserPremium,
                                    showsNutrientPreview = showsNutrientPreview,
                                    onClick = { onFoodClick(food) }
                                )
                            }
                        }
                    }

                    else -> {}
                }

                item("foodListState_errorMessage") {
                    NotFoundMessageAnimated(
                        isVisible = state is UiState.Error,
                        message = state.toErrorMessageOrNull() ?: ""
                    )
                }
            }
        }
    }

    data class FoodInformationComposables(
        val food: FoodInformation,
        val nutrients: NutrientsByType<NutrientDataWithAmount> = food.nutrients,
        val user: User
    ) {
        @Composable
        fun BaseInformation(
            /**
             * Horizontal alignment in between the brand and name
             */
            topHorizontalAlignment: Alignment.Horizontal = Alignment.Start,

            /**
             * Horizontal alignment for the amount per serving
             */
            bottomHorizontalAlignment: Alignment.Horizontal = Alignment.End,


            /**
             * Vertical space between the brand, name, and amount per serving
             */
            verticalSpace: Dp = 0.dp,

            foodLogServings: Double? = 1.0
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = bottomHorizontalAlignment,
                    verticalArrangement = Arrangement.spacedBy(verticalSpace)
                ) {
                    val brandColor = Ui.getFoodBrandColor()

                    Column(
                        horizontalAlignment = topHorizontalAlignment,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace)
                    ) {
                        val foodBrand = food.information.brand?.ifEmpty { "~" } ?: "~"

                        Text(
                            text = foodBrand,
                            style = MaterialTheme.typography.labelMedium,
                            color = brandColor
                        )

                        Text(
                            text = food.information.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = if (foodLogServings != null) null else {
                                TextAlign.Center
                            }
                        )
                    }

                    if (foodLogServings != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            val amPerSer = doubleFormatter(
                                value = food.information.amountPerServing * foodLogServings
                            )

                            Text(
                                text = amPerSer,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Default,
                                color = brandColor
                            )

                            Text(
                                text = food.information.servingUnit,
                                style = MaterialTheme.typography.bodySmall,
                                color = brandColor
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun NutrientSummary() {
            Column(
                modifier = Modifier.areaContainer(size = AreaContainerSize.MEDIUM),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    PagedNutrients(
                        nutrients = nutrients.basic,
                        displayFormat = UNutrient.ScrollableNutrientsFormat.CIRCLE,
                        isUserPremium = user.isPremium
                    )
                }
            )
        }

        @Composable
        fun RemainingNutrients() {
            val remainingNutrients = listOf(
                NutrientType.VITAMIN to nutrients.vitamin,
                NutrientType.MINERAL to nutrients.mineral
            )

            if (remainingNutrients.any { it.second.isNotEmpty() }) {
                Column(
                    modifier = Modifier.areaContainer(size = AreaContainerSize.MEDIUM),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        remainingNutrients.forEachIndexed { index, (type, ns) ->
                            val title = type.name.lowercase().replaceFirstChar { it.uppercase() }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                content = {
                                    if (ns.isNotEmpty()) {
                                        Text(
                                            text = "${title}s",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        NutrientsAsLine(ns)
                                    }
                                }
                            )

                            // Space between each nutrient category mini section
                            if (index < remainingNutrients.lastIndex && ns.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun FoodFormScreen(
        title: String,
        currentStep: Int,
        formState: FormStates.FoodCreation,
        nutrientDvControls: INutrientDvControls.NutrientDvControls
    ) {

    }

    object Debug {
        fun FoodInformation.logFoodInformation() {
            logcat("[${this.information.id}] - food information:")
            logcat("  ${this.information.id}")
            logcat("  ${this.information.name}")
            logcat("  ${this.information.brand}")
            logcat("  ${this.information.amountPerServing}")
            logcat("  ${this.information.servingUnit}")
        }

        fun FoodInformation.logFoodMetadata() {
            logcat("[${this.information.id}] - food metadata:")
            logcat("  ${this.metadata.isFavorite}")
            logcat("  ${this.metadata.lastLoggedAt}")
            logcat("  ${this.metadata.updatedAt}")
            logcat("  ${this.metadata.createdAt}")
        }

        fun FoodInformation.logFoodNutrients() {
            logcat("[${this.information.id}] - food nutrients:")
            this.nutrients.combine().forEach {
                it.logNutrientWithAmountData()
            }
        }
    }
}