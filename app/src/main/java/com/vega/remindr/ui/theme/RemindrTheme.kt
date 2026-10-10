package com.vega.remindr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ThemeOption(
    val key: String,
    val label: String,
    val description: String,
    val swatches: List<Color>
) {
    CORAL("coral", "Coral", "Coral quente e petróleo", listOf(Color(0xFF9B4034), Color(0xFF006C68), Color(0xFFFFF7F4))),
    BERRY("berry", "Framboesa", "Vinho vibrante e dourado", listOf(Color(0xFF963B5B), Color(0xFF775A00), Color(0xFFFFF7F8))),
    MIDNIGHT("midnight", "Meia-noite", "Azul elétrico no escuro", listOf(Color(0xFFB5C6FF), Color(0xFF8BE9E1), Color(0xFF0F141C)));

    companion object {
        fun fromKey(key: String): ThemeOption = entries.firstOrNull { it.key == key } ?: MIDNIGHT
    }
}

@Composable
fun RemindrTheme(
    option: ThemeOption,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (option) {
        ThemeOption.CORAL -> if (darkTheme) deriveScheme(coralScheme, true) else coralScheme
        ThemeOption.BERRY -> if (darkTheme) deriveScheme(berryScheme, true) else berryScheme
        ThemeOption.MIDNIGHT -> if (darkTheme) midnightScheme else deriveScheme(midnightScheme, false)
    }
    MaterialTheme(colorScheme = colorScheme, typography = RemindrTypography, shapes = RemindrShapes, content = content)
}

private val RemindrTypography = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 42.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp)
)

private val RemindrShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

private val coralScheme = lightColorScheme(
    primary = Color(0xFF9B4034), onPrimary = Color.White, primaryContainer = Color(0xFFFFDAD4), onPrimaryContainer = Color(0xFF3F0503),
    secondary = Color(0xFF006C68), onSecondary = Color.White, secondaryContainer = Color(0xFF74F7F0), onSecondaryContainer = Color(0xFF00201F),
    tertiary = Color(0xFF775B00), onTertiary = Color.White, tertiaryContainer = Color(0xFFFFE08A), onTertiaryContainer = Color(0xFF241A00),
    background = Color(0xFFFFF8F6), onBackground = Color(0xFF221A18), surface = Color(0xFFFFF8F6), onSurface = Color(0xFF221A18),
    surfaceVariant = Color(0xFFF2DEDA), onSurfaceVariant = Color(0xFF51433F), outline = Color(0xFF83736E), error = Color(0xFFBA1A1A)
)

private val berryScheme = lightColorScheme(
    primary = Color(0xFF963B5B), onPrimary = Color.White, primaryContainer = Color(0xFFFFD9E2), onPrimaryContainer = Color(0xFF3E001D),
    secondary = Color(0xFF775A00), onSecondary = Color.White, secondaryContainer = Color(0xFFFFE08A), onSecondaryContainer = Color(0xFF251A00),
    tertiary = Color(0xFF386A20), onTertiary = Color.White, tertiaryContainer = Color(0xFFB8F397), onTertiaryContainer = Color(0xFF062100),
    background = Color(0xFFFFF8F9), onBackground = Color(0xFF21191B), surface = Color(0xFFFFF8F9), onSurface = Color(0xFF21191B),
    surfaceVariant = Color(0xFFF0DEE2), onSurfaceVariant = Color(0xFF504447), outline = Color(0xFF827376), error = Color(0xFFBA1A1A)
)

private val midnightScheme = darkColorScheme(
    primary = Color(0xFFB5C6FF), onPrimary = Color(0xFF002B60), primaryContainer = Color(0xFF12457D), onPrimaryContainer = Color(0xFFD9E2FF),
    secondary = Color(0xFF8BE9E1), onSecondary = Color(0xFF003735), secondaryContainer = Color(0xFF00504C), onSecondaryContainer = Color(0xFFA7F5ED),
    tertiary = Color(0xFFFFDDA5), onTertiary = Color(0xFF412D00), tertiaryContainer = Color(0xFF5B4100), onTertiaryContainer = Color(0xFFFFE2A8),
    background = Color(0xFF0F141C), onBackground = Color(0xFFE0E2EA), surface = Color(0xFF0F141C), onSurface = Color(0xFFE0E2EA),
    surfaceVariant = Color(0xFF43474F), onSurfaceVariant = Color(0xFFC3C6D0), outline = Color(0xFF8D919A), error = Color(0xFFFFB4AB)
)


internal fun deriveScheme(base: ColorScheme, dark: Boolean): ColorScheme {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(base.primary.toArgb(), hsv)
    fun tone(sat: Float, value: Float): Color = Color.hsv(hsv[0], sat, value)
    fun accent(color: Color): Color = when {
        dark && color.luminance() < 0.28f -> lerp(color, Color.White, 0.40f)
        !dark && color.luminance() > 0.45f -> lerp(color, Color.Black, 0.32f)
        else -> color
    }
    fun contentOn(color: Color): Color = if (color.luminance() > 0.5f) Color(0xFF111111) else Color.White

    val primary = accent(base.primary)
    val secondary = accent(base.secondary)
    val tertiary = accent(base.tertiary)
    val background = if (dark) tone(0.30f, 0.08f) else tone(0.05f, 0.985f)

    return base.copy(
        primary = primary,
        onPrimary = contentOn(primary),
        primaryContainer = if (dark) lerp(primary, Color.Black, 0.62f) else lerp(primary, Color.White, 0.80f),
        onPrimaryContainer = if (dark) lerp(primary, Color.White, 0.80f) else lerp(primary, Color.Black, 0.65f),
        secondary = secondary,
        onSecondary = contentOn(secondary),
        secondaryContainer = if (dark) lerp(secondary, Color.Black, 0.62f) else lerp(secondary, Color.White, 0.80f),
        onSecondaryContainer = if (dark) lerp(secondary, Color.White, 0.80f) else lerp(secondary, Color.Black, 0.65f),
        tertiary = tertiary,
        onTertiary = contentOn(tertiary),
        tertiaryContainer = if (dark) lerp(tertiary, Color.Black, 0.62f) else lerp(tertiary, Color.White, 0.80f),
        onTertiaryContainer = if (dark) lerp(tertiary, Color.White, 0.80f) else lerp(tertiary, Color.Black, 0.65f),
        background = background,
        onBackground = if (dark) tone(0.06f, 0.95f) else tone(0.30f, 0.10f),
        surface = background,
        onSurface = if (dark) tone(0.06f, 0.95f) else tone(0.30f, 0.10f),
        surfaceVariant = if (dark) tone(0.22f, 0.20f) else tone(0.09f, 0.92f),
        onSurfaceVariant = if (dark) tone(0.10f, 0.74f) else tone(0.20f, 0.38f),
        outline = if (dark) tone(0.12f, 0.52f) else tone(0.12f, 0.50f),
        outlineVariant = if (dark) tone(0.20f, 0.30f) else tone(0.12f, 0.82f),
        surfaceContainerLowest = if (dark) tone(0.30f, 0.05f) else Color.White,
        surfaceContainerLow = if (dark) tone(0.28f, 0.11f) else tone(0.06f, 0.965f),
        surfaceContainer = if (dark) tone(0.26f, 0.14f) else tone(0.07f, 0.945f),
        surfaceContainerHigh = if (dark) tone(0.24f, 0.18f) else tone(0.08f, 0.925f),
        surfaceContainerHighest = if (dark) tone(0.22f, 0.22f) else tone(0.09f, 0.90f),
        error = if (dark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A),
        onError = if (dark) Color(0xFF690005) else Color.White,
        errorContainer = if (dark) Color(0xFF93000A) else Color(0xFFFFDAD6),
        onErrorContainer = if (dark) Color(0xFFFFDAD6) else Color(0xFF410002)
    )
}
