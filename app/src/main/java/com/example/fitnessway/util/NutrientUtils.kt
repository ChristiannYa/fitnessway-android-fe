package com.example.fitnessway.util

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.fitnessway.data.model.nutrient.NutrientIntake
import com.example.fitnessway.data.model.nutrient.NutrientType
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.ui.theme.robotoSerifFamily
import kotlin.math.roundToInt

data class NutrientData(
    val intake: Float,
    val progress: Float,
    val remaining: Float,
    val over: Float,
    val isGoalMet: Boolean,
    val isOverGoal: Boolean
)

fun calcNutrientData(n: NutrientIntake): NutrientData {
    val intake = (n.intake * 10.0f).roundToInt() / 10.0f
    val goal = n.goal ?: 0f

    val progress = if (goal > 0) {
        ((n.intake / goal) * 100).coerceIn(0f, 100f)
    } else {
        0f
    }

    val remaining = (goal - n.intake).let {
        (it * 10.0f).roundToInt() / 10.0f
    }

    val over = (n.intake - goal).let {
        (it * 10.0f).roundToInt() / 10.0f
    }

    return NutrientData(
        intake = intake,
        progress = progress,
        remaining = remaining,
        over = over,
        isGoalMet = remaining == 0f,
        isOverGoal = remaining < 0
    )
}

fun filterDisplayedNutrients(
    nutrients: List<NutrientIntake>,
    user: User
): List<NutrientIntake> {
    return nutrients.filter {
        it.goal != null && (!it.nutrient.isPremium || user.isPremium)
    }
}

val intakeTextStyle = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.SemiBold
)

val intakeNumStyle = TextStyle(
    fontFamily = robotoSerifFamily,
    fontWeight = FontWeight.SemiBold
)