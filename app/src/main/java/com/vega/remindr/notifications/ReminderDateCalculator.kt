package com.vega.remindr.notifications

import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime

internal object ReminderDateCalculator {
    private val reminderTime = LocalTime.of(9, 0)

    fun nextTrigger(birthDate: LocalDate, now: ZonedDateTime): ZonedDateTime {
        val zone = now.zone
        var trigger = occurrenceInYear(birthDate, now.year, zone)
        if (!trigger.isAfter(now)) trigger = occurrenceInYear(birthDate, now.year + 1, zone)
        return trigger
    }

    private fun occurrenceInYear(birthDate: LocalDate, year: Int, zone: ZoneId): ZonedDateTime {
        val lastDay = birthDate.month.length(Year.isLeap(year.toLong()))
        val date = LocalDate.of(year, birthDate.month, minOf(birthDate.dayOfMonth, lastDay))
        return ZonedDateTime.of(date, reminderTime, zone)
    }
}
