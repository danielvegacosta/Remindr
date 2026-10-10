package com.vega.remindr.ui

import com.vega.remindr.model.Birthday
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeScreenFilterTest {
    @Test
    fun monthFilterExcludesBirthdaysThatAlreadyPassed() {
        val today = LocalDate.of(2026, 10, 9)
        val birthdays = listOf(
            Birthday(id = 1, name = "Passou", birthDate = LocalDate.of(1990, 10, 8)),
            Birthday(id = 2, name = "Hoje", birthDate = LocalDate.of(1995, 10, 9)),
            Birthday(id = 3, name = "Futuro", birthDate = LocalDate.of(2000, 10, 31)),
            Birthday(id = 4, name = "Mês passado", birthDate = LocalDate.of(1980, 9, 30)),
            Birthday(id = 5, name = "Próximo mês", birthDate = LocalDate.of(1985, 11, 1))
        )

        val result = filterBirthdaysForMonth(birthdays, today)

        assertEquals(listOf("Hoje", "Futuro"), result.map { it.name })
    }

    @Test
    fun appearanceModeControlsDarkThemeIndependentlyOfPalette() {
        assertEquals(true, resolveDarkTheme(AppearancePrefs(followSystem = true, dark = false), systemDark = true))
        assertEquals(false, resolveDarkTheme(AppearancePrefs(followSystem = false, dark = false), systemDark = true))
        assertEquals(true, resolveDarkTheme(AppearancePrefs(followSystem = false, dark = true), systemDark = false))
        assertEquals(true, resolveDarkTheme(AppearancePrefs(followSystem = false, dark = null), systemDark = false))
    }

    @Test
    fun monthFilterIncludesBirthdayOnTheCurrentDay() {
        val today = LocalDate.of(2026, 10, 9)
        val birthday = Birthday(name = "Hoje", birthDate = LocalDate.of(2001, 10, 9))

        assertEquals(listOf(birthday), filterBirthdaysForMonth(listOf(birthday), today))
    }
}
