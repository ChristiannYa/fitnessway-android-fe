package com.example.fitnessway

import com.example.fitnessway.util.extensions.toPrecisedString
import org.junit.Test
import kotlin.test.assertEquals

class DoubleFormatterTest {

    @Test
    fun `test floating point precision issues`() {
        assertEquals("110", 109.99999999999999.toPrecisedString())
        assertEquals("0", (-1.4210854715202004E-14).toPrecisedString())
        assertEquals("73.7", 73.69999999999999.toPrecisedString())
    }

    @Test
    fun `test required cases`() {
        assertEquals("110", 109.99999999999999.toPrecisedString())
        assertEquals("120", 120.0.toPrecisedString())
        assertEquals("30.2", 30.2041.toPrecisedString())
        assertEquals("53", 53.090.toPrecisedString())
        assertEquals("10.1", 10.100.toPrecisedString())
        assertEquals("109.9", 109.95.toPrecisedString())
        assertEquals("110", 110.099.toPrecisedString())
        assertEquals("109.9", 109.9.toPrecisedString())
        assertEquals("108.9", 108.99.toPrecisedString())
        assertEquals("10.8", 10.892.toPrecisedString())
        assertEquals("50", 49.98.toPrecisedString(0))
        assertEquals("9.9", 9.99.toPrecisedString())
        assertEquals("9.9", 9.98.toPrecisedString())
        assertEquals("5.8", 5.895.toPrecisedString())
        assertEquals("5.8", 5.896.toPrecisedString())
    }

    @Test
    fun `test subtraction operations`() {
        val result1 = 183.7 - 73.7
        assertEquals("110", result1.toPrecisedString())

        val result2 = 183.7 - 110.0
        assertEquals("73.7", result2.toPrecisedString())
    }

    @Test
    fun `test edge cases`() {
        assertEquals("0", 0.0.toPrecisedString())
        assertEquals("0", 0.04.toPrecisedString())
        assertEquals("0", 0.05.toPrecisedString())
        assertEquals("-5.8", (-5.85).toPrecisedString())
    }
}