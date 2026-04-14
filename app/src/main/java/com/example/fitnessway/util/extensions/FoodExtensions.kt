package com.example.fitnessway.util.extensions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fitnessway.R
import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.FoodBase
import com.example.fitnessway.data.model.m_26.ListOption
import com.example.fitnessway.data.model.m_26.PendingFoodStatus
import com.example.fitnessway.data.model.m_26.ServingUnit
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.Emerald
import com.example.fitnessway.ui.theme.ImperialRed
import com.example.fitnessway.ui.theme.OrangeLight

fun List<MFood.Model.FoodInformation>.findById(id: Int) = find { it.information.id == id }

fun FoodBase.calcAmountPerServing() = if (this.servingUnit == ServingUnit.OZ) {
    this.amountPerServing * 28.3495
} else this.amountPerServing

fun PendingFoodStatus.getAccent(): Color = when (this) {
    PendingFoodStatus.PENDING -> OrangeLight
    PendingFoodStatus.APPROVED -> Emerald
    PendingFoodStatus.REJECTED -> ImperialRed
}

fun PendingFoodStatus.getImageVector(): ImageVector = when (this) {
    PendingFoodStatus.PENDING -> Icons.Default.AccessTime
    PendingFoodStatus.APPROVED -> Icons.Default.CheckCircleOutline
    PendingFoodStatus.REJECTED -> Icons.Default.ErrorOutline
}

fun ListOption.getAppIconSource(): Structure.AppIconSource =
    when (this) {
        ListOption.Food -> Structure.AppIconSource.Resource(R.drawable.food)
        ListOption.PendingFood -> Structure.AppIconSource.Vector(Icons.Default.AddChart)
        ListOption.Supplement -> Structure.AppIconSource.Resource(R.drawable.energy)
    }