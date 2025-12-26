package com.example.fitnessway

import com.example.fitnessway.util.Formatters.doubleFormatter
import org.junit.Test
import kotlin.test.assertEquals

class DoubleFormatterTest {

    @Test
    fun `test floating point precision issues`() {
        assertEquals("110", doubleFormatter(109.99999999999999, 1))
        assertEquals("0", doubleFormatter(-1.4210854715202004E-14, 1))
        assertEquals("73.7", doubleFormatter(73.69999999999999, 1))
    }

    @Test
    fun `test required cases`() {
        assertEquals("110", doubleFormatter(109.99999999999999, 1))
        assertEquals("120", doubleFormatter(120.0, 1))
        assertEquals("30.2", doubleFormatter(30.2041, 1))
        assertEquals("53", doubleFormatter(53.090, 1))
        assertEquals("10.1", doubleFormatter(10.100, 1))
        assertEquals("109.9", doubleFormatter(109.95, 1))
        assertEquals("110", doubleFormatter(110.099, 1))
        assertEquals("109.9", doubleFormatter(109.9, 1))
        assertEquals("108.9", doubleFormatter(108.99, 1))
        assertEquals("10.8", doubleFormatter(10.892, 1))
        assertEquals("50", doubleFormatter(49.98, 0))
        assertEquals("9.9", doubleFormatter(9.99, 1))
        assertEquals("9.9", doubleFormatter(9.98, 1))
        assertEquals("5.8", doubleFormatter(5.895, 1))
        assertEquals("5.8", doubleFormatter(5.896, 1))
    }

    @Test
    fun `test subtraction operations`() {
        val result1 = 183.7 - 73.7
        assertEquals("110", doubleFormatter(result1, 1))

        val result2 = 183.7 - 110.0
        assertEquals("73.7", doubleFormatter(result2, 1))
    }

    @Test
    fun `test edge cases`() {
        assertEquals("0", doubleFormatter(0.0, 1))
        assertEquals("0", doubleFormatter(0.04, 1))
        assertEquals("0", doubleFormatter(0.05, 1))
        assertEquals("-5.8", doubleFormatter(-5.85, 1))
    }
}