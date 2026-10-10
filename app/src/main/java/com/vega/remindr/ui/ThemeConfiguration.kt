@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.vega.remindr.ui.theme.ThemeOption

internal data class AppearancePrefs(val followSystem: Boolean = false, val dark: Boolean? = null)

internal fun resolveDarkTheme(appearance: AppearancePrefs, systemDark: Boolean): Boolean = when {
    appearance.followSystem -> systemDark
    appearance.dark != null -> appearance.dark == true
    else -> true
}

internal const val FALLBACK_DEFAULT_THEME_KEY = "meia-noite"
internal const val OCEAN_THEME_KEY = "oceano"
internal const val FOREST_THEME_KEY = "floresta"
internal const val LEGACY_DEFAULT_THEME_KEY = "padrao"
internal const val LEGACY_OCEAN_DEFAULT_KEY = "oceano"
internal const val AURORA_THEME_KEY = "aurora"

internal val MidnightThemeNames = setOf(
    "meia-noite", "meia noite", "meianoite", "midnight"
)

internal fun ThemeOption.normalizedNames(): Set<String> = setOf(
    label.toString().trim().lowercase(PtBr),
    key.toString().trim().lowercase(PtBr)
)

internal fun ThemeOption.isMidnightTheme(): Boolean =
    normalizedNames().any { it in MidnightThemeNames }

internal fun midnightThemeOption(): ThemeOption? =
    ThemeOption.entries.firstOrNull { it.isMidnightTheme() }

internal fun defaultThemeKey(): String =
    midnightThemeOption()?.key?.toString()?.trim()?.takeIf { it.isNotEmpty() }
        ?: FALLBACK_DEFAULT_THEME_KEY

internal fun loadAppearance(context: Context): AppearancePrefs {
    val prefs = context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE)
    return AppearancePrefs(
        followSystem = prefs.getBoolean("follow_system", false),
        dark = if (prefs.contains("dark")) prefs.getBoolean("dark", false) else null
    )
}

internal fun saveAppearance(context: Context, value: AppearancePrefs) {
    val editor = context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE).edit()
    editor.putBoolean("follow_system", value.followSystem)
    val dark = value.dark
    if (dark != null) editor.putBoolean("dark", dark) else editor.remove("dark")
    editor.apply()
}

internal fun loadThemeKey(context: Context): String {
    val saved = context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE)
        .getString("theme_key", null)
        ?.trim()
        .orEmpty()

    val defaultKey = defaultThemeKey()
    return when {
        saved.isEmpty() -> defaultKey
        saved.lowercase(PtBr) == LEGACY_DEFAULT_THEME_KEY -> defaultKey
        saved.lowercase(PtBr) in MidnightThemeNames -> defaultKey
        saved.lowercase(PtBr) == LEGACY_OCEAN_DEFAULT_KEY -> saved
        else -> saved
    }
}

internal fun saveThemeKey(context: Context, key: String) {
    context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE)
        .edit().putString("theme_key", key).apply()
}

internal data class ThemeEntry(
    val key: String,
    val label: String,
    val description: String,
    val colors: List<Color>
)

internal val OceanEntry = ThemeEntry(
    key = OCEAN_THEME_KEY,
    label = "Oceano",
    description = "Azul oceânico",
    colors = listOf(Color(0xFF071A1C), Color(0xFF1696A1), Color(0xFF73C9D1))
)

internal val ForestEntry = ThemeEntry(
    key = FOREST_THEME_KEY,
    label = "Floresta",
    description = "Verde floresta, a identidade do Remindr",
    colors = listOf(Color(0xFF0C100E), Color(0xFF4CC38A), Color(0xFFB4D36A))
)

internal val AuroraEntry = ThemeEntry(
    key = AURORA_THEME_KEY,
    label = "Aurora",
    description = "Violeta, turquesa e um toque rosado",
    colors = listOf(Color(0xFF171525), Color(0xFF7562C4), Color(0xFF75D7C1))
)

internal fun themeEntries(): List<ThemeEntry> {
    val midnight = midnightThemeOption()?.let { option ->
        ThemeEntry(
            key = defaultThemeKey(),
            label = "Padrão",
            description = option.description.toString(),
            colors = option.swatches.toList().take(3)
        )
    }

    return listOfNotNull(midnight, OceanEntry, ForestEntry, AuroraEntry) +
            ThemeOption.entries
                .filterNot { it.isMidnightTheme() }
                .map {
                    ThemeEntry(
                        it.key.toString(),
                        it.label.toString(),
                        it.description.toString(),
                        it.swatches.toList()
                    )
                }
}

internal fun forestScheme(dark: Boolean): ColorScheme = if (dark) {
    darkColorScheme(
        primary = Color(0xFF4CC38A),
        onPrimary = Color(0xFF03281A),
        primaryContainer = Color(0xFF14412D),
        onPrimaryContainer = Color(0xFFBDF1D6),
        secondary = Color(0xFF8DBBA2),
        onSecondary = Color(0xFF0C2A1C),
        secondaryContainer = Color(0xFF223A2D),
        onSecondaryContainer = Color(0xFFCBE8D6),
        tertiary = Color(0xFFB4D36A),
        onTertiary = Color(0xFF1D2A03),
        tertiaryContainer = Color(0xFF384A10),
        onTertiaryContainer = Color(0xFFDDF2A4),
        background = Color(0xFF0C100E),
        onBackground = Color(0xFFE6ECE8),
        surface = Color(0xFF0C100E),
        onSurface = Color(0xFFE6ECE8),
        surfaceVariant = Color(0xFF232B27),
        onSurfaceVariant = Color(0xFFA5B0AA),
        outline = Color(0xFF6D7972),
        outlineVariant = Color(0xFF2B3530),
        surfaceContainerLowest = Color(0xFF070A08),
        surfaceContainerLow = Color(0xFF121714),
        surfaceContainer = Color(0xFF171D1A),
        surfaceContainerHigh = Color(0xFF1E2521),
        surfaceContainerHighest = Color(0xFF262F2A),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )
} else {
    lightColorScheme(
        primary = Color(0xFF1F7A4D),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC6EDD8),
        onPrimaryContainer = Color(0xFF05361F),
        secondary = Color(0xFF4C6A5A),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD1E7DA),
        onSecondaryContainer = Color(0xFF0B2316),
        tertiary = Color(0xFF667A1C),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE0EBA6),
        onTertiaryContainer = Color(0xFF1B2400),
        background = Color(0xFFF5F8F6),
        onBackground = Color(0xFF0F1512),
        surface = Color(0xFFF5F8F6),
        onSurface = Color(0xFF0F1512),
        surfaceVariant = Color(0xFFE0E8E3),
        onSurfaceVariant = Color(0xFF424C46),
        outline = Color(0xFF76817A),
        outlineVariant = Color(0xFFC9D2CC),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFEFF3F0),
        surfaceContainer = Color(0xFFE9EEEB),
        surfaceContainerHigh = Color(0xFFE3E9E5),
        surfaceContainerHighest = Color(0xFFDCE3DF),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )
}

internal fun oceanScheme(dark: Boolean): ColorScheme = if (dark) {
    darkColorScheme(
        primary = Color(0xFF66CBD3),
        onPrimary = Color(0xFF00363A),
        primaryContainer = Color(0xFF004F55),
        onPrimaryContainer = Color(0xFF9EEBF1),
        secondary = Color(0xFF8BC8CF),
        onSecondary = Color(0xFF12363A),
        secondaryContainer = Color(0xFF26484D),
        onSecondaryContainer = Color(0xFFB9E6EA),
        tertiary = Color(0xFF74B9D7),
        onTertiary = Color(0xFF00344B),
        tertiaryContainer = Color(0xFF124C65),
        onTertiaryContainer = Color(0xFFC0E9FF),
        background = Color(0xFF071A1C),
        onBackground = Color(0xFFE7F1F2),
        surface = Color(0xFF071A1C),
        onSurface = Color(0xFFE7F1F2),
        surfaceVariant = Color(0xFF253638),
        onSurfaceVariant = Color(0xFFB7CACC),
        outline = Color(0xFF829699),
        outlineVariant = Color(0xFF394A4D),
        surfaceContainerLowest = Color(0xFF040E10),
        surfaceContainerLow = Color(0xFF0D2022),
        surfaceContainer = Color(0xFF122629),
        surfaceContainerHigh = Color(0xFF192E31),
        surfaceContainerHighest = Color(0xFF20373A),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )
} else {
    lightColorScheme(
        primary = Color(0xFF0B6B73),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFB7EDF1),
        onPrimaryContainer = Color(0xFF002F33),
        secondary = Color(0xFF4A6870),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD0E8EC),
        onSecondaryContainer = Color(0xFF0B252A),
        tertiary = Color(0xFF3F7393),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFC5E7F9),
        onTertiaryContainer = Color(0xFF001E2E),
        background = Color(0xFFF3FAFB),
        onBackground = Color(0xFF10191B),
        surface = Color(0xFFF3FAFB),
        onSurface = Color(0xFF10191B),
        surfaceVariant = Color(0xFFDDE8EA),
        onSurfaceVariant = Color(0xFF3F4A4D),
        outline = Color(0xFF6F7D80),
        outlineVariant = Color(0xFFC2CED1),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFEDF4F5),
        surfaceContainer = Color(0xFFE7EFF0),
        surfaceContainerHigh = Color(0xFFE1EAEC),
        surfaceContainerHighest = Color(0xFFD9E4E6),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )
}

internal fun auroraScheme(dark: Boolean): ColorScheme = if (dark) {
    darkColorScheme(
        primary = Color(0xFFB9A9FF),
        onPrimary = Color(0xFF2D176A),
        primaryContainer = Color(0xFF432681),
        onPrimaryContainer = Color(0xFFE7DEFF),
        secondary = Color(0xFF7DD5C1),
        onSecondary = Color(0xFF08382F),
        secondaryContainer = Color(0xFF205248),
        onSecondaryContainer = Color(0xFFA1F0DB),
        tertiary = Color(0xFFEAA5CB),
        onTertiary = Color(0xFF4A1636),
        tertiaryContainer = Color(0xFF66234F),
        onTertiaryContainer = Color(0xFFFFD9EC),
        background = Color(0xFF14131E),
        onBackground = Color(0xFFEBE7F4),
        surface = Color(0xFF14131E),
        onSurface = Color(0xFFEBE7F4),
        surfaceVariant = Color(0xFF35313E),
        onSurfaceVariant = Color(0xFFC8C1D0),
        outline = Color(0xFF918898),
        outlineVariant = Color(0xFF4A4451),
        surfaceContainerLowest = Color(0xFF0D0C14),
        surfaceContainerLow = Color(0xFF1C1925),
        surfaceContainer = Color(0xFF231F2D),
        surfaceContainerHigh = Color(0xFF2B2635),
        surfaceContainerHighest = Color(0xFF342E40),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )
} else {
    lightColorScheme(
        primary = Color(0xFF6650A4),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE9DDFF),
        onPrimaryContainer = Color(0xFF211047),
        secondary = Color(0xFF2D7567),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFB8F0E3),
        onSecondaryContainer = Color(0xFF002019),
        tertiary = Color(0xFF9C3F7B),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFD9EC),
        onTertiaryContainer = Color(0xFF3B0B2A),
        background = Color(0xFFFBF8FF),
        onBackground = Color(0xFF1B1820),
        surface = Color(0xFFFBF8FF),
        onSurface = Color(0xFF1B1820),
        surfaceVariant = Color(0xFFE7E0EA),
        onSurfaceVariant = Color(0xFF49434D),
        outline = Color(0xFF7A727E),
        outlineVariant = Color(0xFFCAC2CD),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFF5F1F7),
        surfaceContainer = Color(0xFFEEEAF0),
        surfaceContainerHigh = Color(0xFFE8E3EA),
        surfaceContainerHighest = Color(0xFFE1DCE4),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )
}
