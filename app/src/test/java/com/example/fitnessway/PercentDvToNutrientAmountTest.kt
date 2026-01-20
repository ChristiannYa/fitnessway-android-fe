package com.example.fitnessway

import com.example.fitnessway.util.UNutrient.percentDvToNutrientAmount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PercentDvToNutrientAmountTest {
    // -------- Fiber --------

    @Test
    fun `fiber - converts percentage DV to grams correctly`() {
        // DV = 28 g
        assertEquals(2.8, percentDvToNutrientAmount(5, 10.0), 0.0001)
        assertEquals(14.0, percentDvToNutrientAmount(5, 50.0), 0.0)
        assertEquals(28.0, percentDvToNutrientAmount(5, 100.0), 0.0)
    }

    // -------- Vitamins --------

    @Test
    fun `vitamin D - converts percentage DV to mcg correctly`() {
        // DV = 20 mcg
        assertEquals(2.0, percentDvToNutrientAmount(12, 10.0), 0.0)
        assertEquals(4.0, percentDvToNutrientAmount(12, 20.0), 0.0)
        assertEquals(20.0, percentDvToNutrientAmount(12, 100.0), 0.0)
    }

    @Test
    fun `vitamin C - converts percentage DV to mg correctly`() {
        // DV = 90 mg
        assertEquals(9.0, percentDvToNutrientAmount(11, 10.0), 0.0)
        assertEquals(45.0, percentDvToNutrientAmount(11, 50.0), 0.0)
        assertEquals(90.0, percentDvToNutrientAmount(11, 100.0), 0.0)
    }

    @Test
    fun `vitamin B12 - converts percentage DV to mcg correctly`() {
        // DV = 2.4 mcg
        assertEquals(0.48, percentDvToNutrientAmount(10, 20.0), 0.0001)
        assertEquals(2.4, percentDvToNutrientAmount(10, 100.0), 0.0001)
    }

    @Test
    fun `vitamin A - converts percentage DV to mcg correctly`() {
        // DV = 900 mcg
        assertEquals(180.0, percentDvToNutrientAmount(9, 20.0), 0.0)
        assertEquals(900.0, percentDvToNutrientAmount(9, 100.0), 0.0)
    }

    // -------- Minerals --------

    @Test
    fun `potassium - converts percentage DV to mg correctly`() {
        // DV = 4700 mg
        assertEquals(188.0, percentDvToNutrientAmount(16, 4.0), 0.0)
        assertEquals(376.0, percentDvToNutrientAmount(16, 8.0), 0.0)
        assertEquals(4700.0, percentDvToNutrientAmount(16, 100.0), 0.0)
    }

    @Test
    fun `calcium - converts percentage DV to mg correctly`() {
        // DV = 1300 mg
        assertEquals(195.0, percentDvToNutrientAmount(13, 15.0), 0.0)
        assertEquals(26.0, percentDvToNutrientAmount(13, 2.0), 0.0)
    }

    @Test
    fun `iron - converts percentage DV to mg correctly`() {
        // DV = 18 mg
        assertEquals(1.8, percentDvToNutrientAmount(14, 10.0), 0.0)
        assertEquals(2.5, percentDvToNutrientAmount(14, 13.9), 0.01)
        assertEquals(1.2, percentDvToNutrientAmount(14, 6.7), 0.01)
    }

    @Test
    fun `magnesium - converts percentage DV to mg correctly`() {
        // DV = 420 mg
        assertEquals(33.6, percentDvToNutrientAmount(15, 8.0), 0.0)
        assertEquals(420.0, percentDvToNutrientAmount(15, 100.0), 0.0)
    }

    // -------- Edge cases --------

    @Test
    fun `percentage DV over 100 percent is allowed`() {
        // Allowing >100% is intentional (supplements, fortified foods)
        assertEquals(40.0, percentDvToNutrientAmount(12, 200.0), 0.0)
    }

    @Test
    fun `zero percent returns zero`() {
        assertEquals(0.0, percentDvToNutrientAmount(16, 0.0), 0.0)
        assertEquals(0.0, percentDvToNutrientAmount(12, 0.0), 0.0)
    }

    @Test
    fun `unsupported nutrient throws exception`() {
        // Calories, sugar, cholesterol, etc.
        assertThrows(IllegalArgumentException::class.java) {
            percentDvToNutrientAmount(1, 10.0)
        }
    }

    @Test
    fun `real world nutrition scenario percent DV input`() {
        // User enters 10% Vitamin D
        val percent = 10.0

        val result = percentDvToNutrientAmount(12, percent)

        assertEquals(2.0, result, 0.0)
    }
}