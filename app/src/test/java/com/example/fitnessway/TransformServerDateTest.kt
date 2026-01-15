package com.example.fitnessway

import com.example.fitnessway.util.Formatters.transformServerDate
import org.junit.Test
import kotlin.test.assertEquals

class DateTransformTest {

    @Test
    fun `test basic transformation`() {
        assertEquals("2026-01-13 06:14 PM", transformServerDate("2026-01-13 18:14:13.489983"))
    }

    @Test
    fun `test morning times`() {
        assertEquals("2026-01-13 09:30 AM", transformServerDate("2026-01-13 09:30:00.000000"))
        assertEquals("2026-01-13 01:15 AM", transformServerDate("2026-01-13 01:15:45.123456"))
    }

    @Test
    fun `test afternoon and evening times`() {
        assertEquals("2026-01-13 03:45 PM", transformServerDate("2026-01-13 15:45:30.000000"))
        assertEquals("2026-01-13 11:59 PM", transformServerDate("2026-01-13 23:59:59.999999"))
    }

    @Test
    fun `test midnight and noon`() {
        assertEquals("2026-01-13 12:00 AM", transformServerDate("2026-01-13 00:00:00.000000"))
        assertEquals("2026-01-13 12:00 PM", transformServerDate("2026-01-13 12:00:00.000000"))
        assertEquals("2026-01-13 12:30 PM", transformServerDate("2026-01-13 12:30:15.000000"))
    }

    @Test
    fun `test different dates`() {
        assertEquals("2025-12-31 11:45 PM", transformServerDate("2025-12-31 23:45:00.000000"))
        assertEquals("2026-06-15 08:20 AM", transformServerDate("2026-06-15 08:20:30.500000"))
        assertEquals("2024-02-29 02:15 PM", transformServerDate("2024-02-29 14:15:00.000000"))
    }

    @Test
    fun `test various microsecond values`() {
        assertEquals("2026-01-13 06:14 PM", transformServerDate("2026-01-13 18:14:13.000001"))
        assertEquals("2026-01-13 06:14 PM", transformServerDate("2026-01-13 18:14:13.999999"))
        assertEquals("2026-01-13 06:14 PM", transformServerDate("2026-01-13 18:14:13.500000"))
    }

    @Test
    fun `test single digit hours`() {
        assertEquals("2026-01-13 01:05 AM", transformServerDate("2026-01-13 01:05:00.000000"))
        assertEquals("2026-01-13 09:59 AM", transformServerDate("2026-01-13 09:59:59.999999"))
        assertEquals("2026-01-13 01:30 PM", transformServerDate("2026-01-13 13:30:00.000000"))
    }

    @Test
    fun `test edge case times`() {
        assertEquals("2026-01-13 12:01 AM", transformServerDate("2026-01-13 00:01:00.000000"))
        assertEquals("2026-01-13 11:59 AM", transformServerDate("2026-01-13 11:59:00.000000"))
        assertEquals("2026-01-13 12:01 PM", transformServerDate("2026-01-13 12:01:00.000000"))
    }
}