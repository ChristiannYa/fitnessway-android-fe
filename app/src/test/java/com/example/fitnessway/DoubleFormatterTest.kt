package com.example.fitnessway

import com.example.fitnessway.util.extensions.toTruncatedDecimalString
import org.junit.Test
import kotlin.test.assertEquals

class DoubleFormatterTest {

    @Test
    fun `test floating point precision issues`() {
        assertEquals("110", 109.99999999999999.toTruncatedDecimalString())
        assertEquals("0", (-1.4210854715202004E-14).toTruncatedDecimalString())
        assertEquals("73.7", 73.69999999999999.toTruncatedDecimalString())
    }

    @Test
    fun `test required cases`() {
        assertEquals("110", 109.99999999999999.toTruncatedDecimalString())
        assertEquals("120", 120.0.toTruncatedDecimalString())
        assertEquals("30.2", 30.2041.toTruncatedDecimalString())
        assertEquals("53", 53.090.toTruncatedDecimalString())
        assertEquals("10.1", 10.100.toTruncatedDecimalString())
        assertEquals("109.9", 109.95.toTruncatedDecimalString())
        assertEquals("110", 110.099.toTruncatedDecimalString())
        assertEquals("109.9", 109.9.toTruncatedDecimalString())
        assertEquals("108.9", 108.99.toTruncatedDecimalString())
        assertEquals("10.8", 10.892.toTruncatedDecimalString())
        assertEquals("50", 49.98.toTruncatedDecimalString())
        assertEquals("9.9", 9.99.toTruncatedDecimalString())
        assertEquals("9.9", 9.98.toTruncatedDecimalString())
        assertEquals("5.8", 5.895.toTruncatedDecimalString())
        assertEquals("5.8", 5.896.toTruncatedDecimalString())
    }

    @Test
    fun `test subtraction operations`() {
        val result1 = 183.7 - 73.7
        assertEquals("110", result1.toTruncatedDecimalString())

        val result2 = 183.7 - 110.0
        assertEquals("73.7", result2.toTruncatedDecimalString())
    }

    @Test
    fun `test edge cases`() {
        assertEquals("0", 0.0.toTruncatedDecimalString())
        assertEquals("0", 0.04.toTruncatedDecimalString())
        assertEquals("0", 0.05.toTruncatedDecimalString())
        assertEquals("-5.8", (-5.85).toTruncatedDecimalString())
    }
}