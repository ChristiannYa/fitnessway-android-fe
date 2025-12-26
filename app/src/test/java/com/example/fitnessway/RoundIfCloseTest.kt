package com.example.fitnessway

import com.example.fitnessway.util.Formatters.roundIfClose
import org.junit.Assert.assertEquals
import org.junit.Test

class RoundIfCloseTest {
    @Test
    fun `roundIfClose - value very close to whole number rounds up`() {
        assertEquals(50.0, 49.9996.roundIfClose(), 0.0)
        assertEquals(75.0, 74.999.roundIfClose(), 0.0)
        assertEquals(100.0, 99.9995.roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - value very close to whole number rounds down`() {
        assertEquals(50.0, 50.0004.roundIfClose(), 0.0)
        assertEquals(75.0, 75.001.roundIfClose(), 0.0)
        assertEquals(100.0, 100.0005.roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - value not close to whole number stays unchanged`() {
        assertEquals(74.76, 74.76.roundIfClose(), 0.0)
        assertEquals(49.5, 49.5.roundIfClose(), 0.0)
        assertEquals(23.45, 23.45.roundIfClose(), 0.0)
        assertEquals(99.85, 99.85.roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - exact whole numbers stay unchanged`() {
        assertEquals(50.0, 50.0.roundIfClose(), 0.0)
        assertEquals(100.0, 100.0.roundIfClose(), 0.0)
        assertEquals(0.0, 0.0.roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - negative values close to whole number round correctly`() {
        assertEquals(-50.0, (-49.9996).roundIfClose(), 0.0)
        assertEquals(-75.0, (-74.999).roundIfClose(), 0.0)
        assertEquals(-100.0, (-100.0005).roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - negative values not close stay unchanged`() {
        assertEquals(-74.76, (-74.76).roundIfClose(), 0.0)
        assertEquals(-49.5, (-49.5).roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - edge case at exactly threshold distance`() {
        // Default threshold is 0.01
        assertEquals(50.0, 50.01.roundIfClose(), 0.0)  // Exactly at threshold, should round
        assertEquals(50.0, 49.99.roundIfClose(), 0.0)  // Exactly at threshold, should round
    }

    @Test
    fun `roundIfClose - just beyond threshold stays unchanged`() {
        assertEquals(50.011, 50.011.roundIfClose(), 0.001)  // Just beyond 0.01 threshold
        assertEquals(49.988, 49.988.roundIfClose(), 0.001)  // Just beyond 0.01 threshold
    }

    @Test
    fun `roundIfClose - custom threshold works correctly`() {
        // Using 0.02 threshold (2%)
        assertEquals(50.0, 49.985.roundIfClose(threshold = 0.02), 0.0)
        assertEquals(50.0, 50.015.roundIfClose(threshold = 0.02), 0.0)
        assertEquals(49.97, 49.97.roundIfClose(threshold = 0.02), 0.0)  // Beyond 0.02
    }

    @Test
    fun `roundIfClose - very small values`() {
        assertEquals(0.0, 0.005.roundIfClose(), 0.0)
        assertEquals(1.0, 0.995.roundIfClose(), 0.0)
        assertEquals(0.5, 0.5.roundIfClose(), 0.0)  // Not close to whole number
    }

    @Test
    fun `roundIfClose - large values`() {
        assertEquals(1000.0, 999.9996.roundIfClose(), 0.0)
        assertEquals(10000.0, 10000.0004.roundIfClose(), 0.0)
        assertEquals(9999.5, 9999.5.roundIfClose(), 0.0)  // Not close
    }

    @Test
    fun `roundIfClose - real-world nutrition app scenario`() {
        val servings = 1.7857
        val foodAmountPerServing = 28.0
        val calc = servings * foodAmountPerServing

        assertEquals(50.0, calc.roundIfClose(), 0.0)
    }

    @Test
    fun `roundIfClose - should not round medium distances`() {
        // User enters 2.67 servings, should get 74.76, not 75
        val servings = 2.67
        val foodAmountPerServing = 28.0
        val calc = servings * foodAmountPerServing

        assertEquals(74.76, calc.roundIfClose(), 0.001)
    }
}