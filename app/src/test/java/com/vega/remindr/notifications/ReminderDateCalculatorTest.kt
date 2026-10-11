package com.vega.remindr.notifications

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderDateCalculatorTest {
    @Test
    fun schedulesAtNineOnBirthdayWhenTimeHasNotPassed() {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = ZonedDateTime.of(2026, 10, 10, 8, 0, 0, 0, zone)

        val trigger = ReminderDateCalculator.nextTrigger(LocalDate.of(1994, 10, 10), now)

        assertEquals(ZonedDateTime.of(2026, 10, 10, 9, 0, 0, 0, zone), trigger)
    }

    @Test
    fun movesToNextYearWhenNineOClockHasPassed() {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = ZonedDateTime.of(2026, 10, 10, 9, 0, 1, 0, zone)

        val trigger = ReminderDateCalculator.nextTrigger(LocalDate.of(1994, 10, 10), now)

        assertEquals(ZonedDateTime.of(2027, 10, 10, 9, 0, 0, 0, zone), trigger)
    }

    @Test
    fun februaryTwentyNinthUsesLastValidDayInNonLeapYears() {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = ZonedDateTime.of(2025, 2, 27, 10, 0, 0, 0, zone)

        val trigger = ReminderDateCalculator.nextTrigger(LocalDate.of(2000, 2, 29), now)

        assertEquals(ZonedDateTime.of(2025, 2, 28, 9, 0, 0, 0, zone), trigger)
    }

    @Test
    fun nextTriggerUsesTheCurrentSystemZone() {
        val zone = ZoneId.of("Asia/Tokyo")
        val now = ZonedDateTime.of(2026, 1, 1, 10, 0, 0, 0, zone)

        val trigger = ReminderDateCalculator.nextTrigger(LocalDate.of(1990, 1, 2), now)

        assertEquals(zone, trigger.zone)
        assertEquals(ZonedDateTime.of(2026, 1, 2, 9, 0, 0, 0, zone), trigger)
    }
}
