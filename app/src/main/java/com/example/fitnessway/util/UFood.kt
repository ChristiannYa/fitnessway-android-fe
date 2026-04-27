package com.example.fitnessway.util

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toMFoodPreview
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.nutrient.NutrientsViewFormat
import com.example.fitnessway.ui.nutrient.PagedNutrients
import com.example.fitnessway.ui.shared.Loading
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Messages.NotFoundMessageAnimated
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.foodContainer
import com.example.fitnessway.util.Formatters.doubleFormatter
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UNutrient.Debug.logNutrientWithAmountData
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsLine
import com.example.fitnessway.util.UNutrient.combine
import com.example.fitnessway.util.UNutrient.getColor

object UFood {
    fun List<FoodInformation>.getFoodById(id: Int): FoodInformation? {
        return this.find { it.information.id == id }
    }

    object Ui {
        fun getFoodBrandText(brand: String?): String {
            return if (brand.isNullOrEmpty()) {
                "~"
            } else brand
        }

        @Composable
        fun getFoodBrandColor(): Color {
            return MaterialTheme.colorScheme.onBackground.copy(0.6f)
        }

        @Composable
        fun FoodPreview(
            food: FoodPreview,
            isUserPremium: Boolean = false,
            showsNutrientPreview: Boolean = false,
            contentRight: (@Composable () -> Unit)? = null,
            onClick: (() -> Unit)? = null
        ) {
            val view = LocalView.current
            val missingBrand = food.base.brand.isNullOrEmpty()

            Box(
                modifier = Modifier.foodContainer {
                    onClick?.let {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        it()
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = food.base.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (missingBrand) "~" else food.base.brand,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isUserPremium && showsNutrientPreview) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                food.nutrientPreview.toList().forEach { n ->
                                    val nutrientColor = getColor(n.color)

                                    if (nutrientColor != null && n.amount != null) {
                                        Text(
                                            text = doubleFormatter(n.amount),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = nutrientColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    contentRight?.let { it() }
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
                                    food = food.toMFoodPreview(),
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
        val edibleInformation: com.example.fitnessway.data.model.m_26.EdibleInformation,
        val isUserPremium: Boolean,
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
                        val foodBrand = edibleInformation.base.brand?.ifEmpty { "~" } ?: "~"

                        Text(
                            text = foodBrand,
                            style = MaterialTheme.typography.labelMedium,
                            color = brandColor
                        )

                        Text(
                            text = edibleInformation.base.name,
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
                                value = edibleInformation.base.amountPerServing * foodLogServings
                            )

                            Text(
                                text = amPerSer,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Default,
                                color = brandColor
                            )

                            Text(
                                text = edibleInformation.base.servingUnit.name.lowercase(),
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
                        nutrients = edibleInformation.nutrients.basic.toList(),
                        viewFormat = NutrientsViewFormat.CIRCLE,
                        isUserPremium = isUserPremium
                    )
                }
            )
        }

        @Composable
        fun RemainingNutrients() {
            val remainingNutrients = listOf(
                NutrientType.VITAMIN to edibleInformation.nutrients.vitamins,
                NutrientType.MINERAL to edibleInformation.nutrients.minerals
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