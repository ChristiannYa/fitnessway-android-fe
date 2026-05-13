package com.example.fitnessway.util.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.OptimisticUpdate
import com.example.fitnessway.data.model.m_26.PaginationData
import kotlin.math.ceil

fun PaginationData.calc(updateType: OptimisticUpdate, offset: Long): PaginationData {
    val newTotalCount = when (updateType) {
        OptimisticUpdate.REMOVE -> this.totalCount - 1
        OptimisticUpdate.ROLLBACK -> this.totalCount + 1
    }

    return PaginationData(
        totalCount = newTotalCount,
        pageCount = ceil(newTotalCount.toDouble() / Pagination.LIMIT.toDouble()).toInt(),
        currentPage = (offset.toInt() / Pagination.LIMIT) + 1
    )
}

fun Color.toHexCode(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("%02X%02X%02X", red, green, blue)
}

/**
 * @return [hue, saturation, value] as a [FloatArray]
 */
fun Color.toHsv(): FloatArray {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    return hsv
}

fun String.isValidHexColor() = this.length == 6 && this.all { it.isDigit() || it.uppercaseChar() in 'A'..'F' }