package com.vega.remindr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    SYSTEM("system", "Padrão do sistema", "Cores e modo do seu dispositivo", listOf(Color(0xFF6750A4), Color(0xFF006A6A), Color(0xFFE8DEF8))),
    LIGHT("light", "Claro", "Azul limpo para o dia", listOf(Color(0xFF1D5FA7), Color(0xFF006A69), Color(0xFFF7F9FC))),
    DARK("dark", "Escuro", "Contraste discreto para a noite", listOf(Color(0xFFAEC6FF), Color(0xFF8FE9E3), Color(0xFF111418))),
    AURORA("aurora", "Aurora", "Turquesa e azul profundo", listOf(Color(0xFF006A6A), Color(0xFF315E91), Color(0xFFE9F7F5))),
    OCEAN("ocean", "Oceano", "Azul cristalino e índigo", listOf(Color(0xFF00658B), Color(0xFF3C5FAD), Color(0xFFEFF8FF))),
    FOREST("forest", "Floresta", "Verde vivo e âmbar suave", listOf(Color(0xFF2D6A24), Color(0xFF745F00), Color(0xFFF3F8EE))),
    CORAL("coral", "Coral", "Coral quente e petróleo", listOf(Color(0xFF9B4034), Color(0xFF006C68), Color(0xFFFFF7F4))),
    BERRY("berry", "Framboesa", "Vinho vibrante e dourado", listOf(Color(0xFF963B5B), Color(0xFF775A00), Color(0xFFFFF7F8))),
    MIDNIGHT("midnight", "Meia-noite", "Azul elétrico no escuro", listOf(Color(0xFFB5C6FF), Color(0xFF8BE9E1), Color(0xFF0F141C)));

    companion object {
        fun fromKey(key: String): ThemeOption = entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}

@Composable
fun RemindrTheme(option: ThemeOption, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colorScheme = when (option) {
        ThemeOption.SYSTEM -> if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        ThemeOption.LIGHT -> clearScheme
        ThemeOption.DARK -> darkScheme
        ThemeOption.AURORA -> auroraScheme
        ThemeOption.OCEAN -> oceanScheme
        ThemeOption.FOREST -> forestScheme
        ThemeOption.CORAL -> coralScheme
        ThemeOption.BERRY -> berryScheme
        ThemeOption.MIDNIGHT -> midnightScheme
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

private val clearScheme = lightColorScheme(
    primary = Color(0xFF1D5FA7), onPrimary = Color.White, primaryContainer = Color(0xFFD7E8FF), onPrimaryContainer = Color(0xFF001C37),
    secondary = Color(0xFF006A69), onSecondary = Color.White, secondaryContainer = Color(0xFF8FF2EF), onSecondaryContainer = Color(0xFF00201F),
    tertiary = Color(0xFF745B00), onTertiary = Color.White, tertiaryContainer = Color(0xFFFFE08A), onTertiaryContainer = Color(0xFF241A00),
    background = Color(0xFFF7F9FC), onBackground = Color(0xFF171C22), surface = Color(0xFFF7F9FC), onSurface = Color(0xFF171C22),
    surfaceVariant = Color(0xFFDEE4EC), onSurfaceVariant = Color(0xFF42474E), outline = Color(0xFF73777F), error = Color(0xFFBA1A1A)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFFAEC6FF), onPrimary = Color(0xFF003061), primaryContainer = Color(0xFF194777), onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = Color(0xFF8FE9E3), onSecondary = Color(0xFF003735), secondaryContainer = Color(0xFF00504D), onSecondaryContainer = Color(0xFFAAF4EF),
    tertiary = Color(0xFFFFDEA1), onTertiary = Color(0xFF3E2E00), tertiaryContainer = Color(0xFF5A4300), onTertiaryContainer = Color(0xFFFFE2A5),
    background = Color(0xFF111418), onBackground = Color(0xFFE1E2E8), surface = Color(0xFF111418), onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF42474F), onSurfaceVariant = Color(0xFFC2C7D0), outline = Color(0xFF8C9199), error = Color(0xFFFFB4AB)
)

private val auroraScheme = lightColorScheme(
    primary = Color(0xFF006A6A), onPrimary = Color.White, primaryContainer = Color(0xFF9CF1F0), onPrimaryContainer = Color(0xFF002020),
    secondary = Color(0xFF315E91), onSecondary = Color.White, secondaryContainer = Color(0xFFD4E3FF), onSecondaryContainer = Color(0xFF001C37),
    tertiary = Color(0xFF6A5F00), onTertiary = Color.White, tertiaryContainer = Color(0xFFF7E95F), onTertiaryContainer = Color(0xFF201C00),
    background = Color(0xFFF4FBFA), onBackground = Color(0xFF161D1D), surface = Color(0xFFF4FBFA), onSurface = Color(0xFF161D1D),
    surfaceVariant = Color(0xFFD8E5E4), onSurfaceVariant = Color(0xFF3F4949), outline = Color(0xFF6F7A79), error = Color(0xFFBA1A1A)
)

private val oceanScheme = lightColorScheme(
    primary = Color(0xFF00658B), onPrimary = Color.White, primaryContainer = Color(0xFFC5EAFF), onPrimaryContainer = Color(0xFF001E2D),
    secondary = Color(0xFF3C5FAD), onSecondary = Color.White, secondaryContainer = Color(0xFFDBE1FF), onSecondaryContainer = Color(0xFF001849),
    tertiary = Color(0xFF785800), onTertiary = Color.White, tertiaryContainer = Color(0xFFFFE08D), onTertiaryContainer = Color(0xFF251A00),
    background = Color(0xFFF5FAFF), onBackground = Color(0xFF171C21), surface = Color(0xFFF5FAFF), onSurface = Color(0xFF171C21),
    surfaceVariant = Color(0xFFDBE4EA), onSurfaceVariant = Color(0xFF3F484D), outline = Color(0xFF6F787E), error = Color(0xFFBA1A1A)
)

private val forestScheme = lightColorScheme(
    primary = Color(0xFF2D6A24), onPrimary = Color.White, primaryContainer = Color(0xFFAFF39F), onPrimaryContainer = Color(0xFF002204),
    secondary = Color(0xFF745F00), onSecondary = Color.White, secondaryContainer = Color(0xFFFFE16F), onSecondaryContainer = Color(0xFF241A00),
    tertiary = Color(0xFF006875), onTertiary = Color.White, tertiaryContainer = Color(0xFF9EEFFD), onTertiaryContainer = Color(0xFF001F25),
    background = Color(0xFFF5F9F0), onBackground = Color(0xFF181D17), surface = Color(0xFFF5F9F0), onSurface = Color(0xFF181D17),
    surfaceVariant = Color(0xFFDFE5D9), onSurfaceVariant = Color(0xFF444940), outline = Color(0xFF747970), error = Color(0xFFBA1A1A)
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
