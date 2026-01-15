package com.example.fitnessway

import com.example.fitnessway.util.Formatters.serverDateToReadableAppDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerDateToReadableAppDateTest {
    @Test
    fun `test already transformed format remains unchanged`() {
        assertEquals("2026-01-13 06:14 PM", serverDateToReadableAppDate("2026-01-13 06:14 PM"))
        assertEquals("2026-01-14 03:20 PM", serverDateToReadableAppDate("2026-01-14 03:20 PM"))
        assertEquals("2026-01-13 09:30 AM", serverDateToReadableAppDate("2026-01-13 09:30 AM"))
    }

    @Test
    fun `test server format gets transformed`() {
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:13.489983")
        )
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:30.375728")
        )
        assertEquals(
            "2026-01-14 03:20 PM",
            serverDateToReadableAppDate("2026-01-14 15:20:37.278153")
        )
    }

    @Test
    fun `test real world food database example`() {
        // Based on your example data
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:13.489983")
        )
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:30.375728")
        )
        assertEquals(
            "2026-01-14 03:20 PM",
            serverDateToReadableAppDate("2026-01-14 15:20:37.278153")
        )
        assertEquals("2026-01-13 06:14 PM", serverDateToReadableAppDate("2026-01-13 06:14 PM"))
    }

    @Test
    fun `test morning server times`() {
        assertEquals(
            "2026-01-13 09:30 AM",
            serverDateToReadableAppDate("2026-01-13 09:30:00.000000")
        )
        assertEquals(
            "2026-01-13 01:15 AM",
            serverDateToReadableAppDate("2026-01-13 01:15:45.123456")
        )
    }

    @Test
    fun `test midnight and noon server times`() {
        assertEquals(
            "2026-01-13 12:00 AM",
            serverDateToReadableAppDate("2026-01-13 00:00:00.000000")
        )
        assertEquals(
            "2026-01-13 12:00 PM",
            serverDateToReadableAppDate("2026-01-13 12:00:00.000000")
        )
        assertEquals(
            "2026-01-13 12:30 PM",
            serverDateToReadableAppDate("2026-01-13 12:30:15.000000")
        )
    }

    @Test
    fun `test midnight and noon transformed format`() {
        assertEquals("2026-01-13 12:00 AM", serverDateToReadableAppDate("2026-01-13 12:00 AM"))
        assertEquals("2026-01-13 12:00 PM", serverDateToReadableAppDate("2026-01-13 12:00 PM"))
    }

    @Test
    fun `test different microsecond values`() {
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:13.000001")
        )
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:13.999999")
        )
        assertEquals(
            "2026-01-13 06:14 PM",
            serverDateToReadableAppDate("2026-01-13 18:14:13.500000")
        )
    }

    @Test
    fun `test various dates`() {
        assertEquals(
            "2025-12-31 11:45 PM",
            serverDateToReadableAppDate("2025-12-31 23:45:00.000000")
        )
        assertEquals(
            "2026-06-15 08:20 AM",
            serverDateToReadableAppDate("2026-06-15 08:20:30.500000")
        )
        assertEquals(
            "2024-02-29 02:15 PM",
            serverDateToReadableAppDate("2024-02-29 14:15:00.000000")
        )
    }
}