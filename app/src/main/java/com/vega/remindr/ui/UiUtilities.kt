@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vega.remindr.model.Birthday
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Locale

internal val PtBr: Locale = Locale("pt", "BR")

internal val CardShape = RoundedCornerShape(24.dp)
internal val FieldShape = RoundedCornerShape(20.dp)
internal val TileShape = RoundedCornerShape(14.dp)
internal val AvatarShape = RoundedCornerShape(percent = 30)

internal fun birthdayDateInYear(date: LocalDate, year: Int): LocalDate {
    val day = minOf(
        date.dayOfMonth,
        date.month.length(Year.of(year).isLeap)
    )

    return LocalDate.of(year, date.month, day)
}

internal fun nextOccurrence(date: LocalDate): LocalDate {
    val today = LocalDate.now()
    val thisYear = birthdayDateInYear(date, today.year)

    return if (thisYear.isBefore(today)) {
        birthdayDateInYear(date, today.year + 1)
    } else {
        thisYear
    }
}

internal fun Birthday.upcomingDate(): LocalDate = nextOccurrence(birthDate)

internal fun Birthday.upcomingAge(): Int = upcomingDate().year - birthDate.year

internal fun yearsWord(age: Int): String = if (age == 1) "ano" else "anos"

internal fun shortDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("d 'de' MMM", PtBr)).replace(".", "")

internal fun rowSubtitle(b: Birthday, withDate: Boolean): String {
    val age = b.upcomingAge()
    val date = shortDate(b.upcomingDate())
    return when {
        age <= 0 -> date
        !withDate -> "Faz $age ${yearsWord(age)}"
        b.daysUntil() == 0L -> "Faz $age ${yearsWord(age)} hoje"
        else -> "Faz $age ${yearsWord(age)} em $date"
    }
}

internal fun groupLabel(days: Long): String = when {
    days == 0L -> "Hoje"
    days <= 7L -> "Próximos 7 dias"
    days <= 30L -> "Próximos 30 dias"
    days <= 90L -> "Próximos 3 meses"
    else -> "Mais adiante"
}

internal fun avatarColors(name: String): Pair<Color, Color> {
    val first = name.trim().firstOrNull()?.uppercaseChar() ?: '?'
    val h1 = (first.code * 137.508f) % 360f
    val h2 = (h1 + 40f) % 360f
    return Color.hsv(h1, 0.55f, 0.92f) to Color.hsv(h2, 0.75f, 0.72f)
}

internal fun monthCells(month: YearMonth): List<Int?> {
    val lead = month.atDay(1).dayOfWeek.value % 7
    val days = (1..month.lengthOfMonth()).toList()
    val base: List<Int?> = List<Int?>(lead) { null } + days
    val pad = (7 - base.size % 7) % 7
    return base + List<Int?>(pad) { null }
}
