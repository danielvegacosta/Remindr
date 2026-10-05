package com.vega.remindr.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Birthday(
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val notes: String = ""
) {
    fun daysUntil(today: LocalDate = LocalDate.now()): Long {
        val day = minOf(birthDate.dayOfMonth, birthDate.month.length(today.isLeapYear))
        var next = LocalDate.of(today.year, birthDate.month, day)
        if (next.isBefore(today)) next = next.plusYears(1)
        return ChronoUnit.DAYS.between(today, next)
    }
}
