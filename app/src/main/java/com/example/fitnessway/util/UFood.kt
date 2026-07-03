package com.example.fitnessway.util

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.fitnessway.data.mappers.toList
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.NutrientData
import com.example.fitnessway.data.model.m_26.NutrientType
import com.example.fitnessway.ui.nutrient.NutrientsViewFormat
import com.example.fitnessway.ui.nutrient.PagedNutrients
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.foodContainer
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UNutrient.Ui.NutrientsAsLine
import com.example.fitnessway.util.UNutrient.getColor
import com.example.fitnessway.util.extensions.toPrecisedString

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
                                            text = n.amount.toPrecisedString(),
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
    }

    data class FoodInformationComposables(
        val edibleInformation: com.example.fitnessway.data.model.m_26.EdibleInformation,
        val apiNutrients: List<NutrientData>,
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
                    verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val brandColor = Ui.getFoodBrandColor()

                    Column(
                        horizontalAlignment = topHorizontalAlignment,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                        modifier = Modifier.fillMaxWidth()
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
                            Text(
                                text = (edibleInformation.base.amountPerServing * foodLogServings).toPrecisedString(),
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
        private fun Modifier.conditionalWrap(condition: Boolean) = this
            .then(
                if (condition)
                    Modifier
                        .areaContainer(size = AreaContainerSize.MEDIUM)
                else Modifier
            )

        @Composable
        fun NutrientSummary(withWrap: Boolean = true) {
            Column(
                modifier = Modifier.conditionalWrap(withWrap),
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
                        isUserPremium = isUserPremium,
                        apiNutrients = apiNutrients
                    )
                }
            )
        }

        @Composable
        fun RemainingNutrients(withWrap: Boolean = true) {
            val remainingNutrients = listOf(
                NutrientType.VITAMIN to edibleInformation.nutrients.vitamin,
                NutrientType.MINERAL to edibleInformation.nutrients.mineral
            )

            if (remainingNutrients.any { it.second.isNotEmpty() }) {
                Column(
                    modifier = Modifier.conditionalWrap(withWrap),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    remainingNutrients.forEachIndexed { index, (type, ns) ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (ns.isNotEmpty()) {
                                Text(
                                    text = "${type.toString().toTitleCase()}s",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                NutrientsAsLine(ns)
                            }
                        }

                        // Space between each nutrient category mini section
                        if (index < remainingNutrients.lastIndex && ns.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
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
    }
}