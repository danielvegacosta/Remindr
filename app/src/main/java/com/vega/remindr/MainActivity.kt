@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr


import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.vega.remindr.data.BackupCodec
import com.vega.remindr.data.RemindrDatabase
import com.vega.remindr.model.Birthday
import com.vega.remindr.notifications.ReminderScheduler
import com.vega.remindr.security.SecurityStore
import com.vega.remindr.ui.theme.RemindrTheme
import com.vega.remindr.ui.theme.ThemeOption
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.material3.SelectableDates
import java.time.Year

class MainActivity : FragmentActivity() {
    private lateinit var database: RemindrDatabase
    private lateinit var security: SecurityStore

    private val buttonNavigation: Boolean by lazy { usesButtonNavigation() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullScreenLayout()
        applySystemBarStyle()
        database = RemindrDatabase(this)
        security = SecurityStore(database)
        setContent {
            var themeKey by remember { mutableStateOf(loadThemeKey(this@MainActivity)) }
            var appearance by remember { mutableStateOf(loadAppearance(this@MainActivity)) }
            val systemDark = isSystemInDarkTheme()
            val view = LocalView.current
            val midnight = midnightThemeOption()
            val midnightKey = defaultThemeKey()
            val option = ThemeOption.entries.firstOrNull {
                !it.isHidden() &&
                        !it.isForestTheme() &&
                        !it.isOceanTheme() &&
                        !it.isAuroraTheme() &&
                        it.key.toString().trim().lowercase(PtBr) == themeKey.trim().lowercase(PtBr)
            }
            val isMidnight = themeKey.trim().lowercase(PtBr) == midnightKey.trim().lowercase(PtBr)
            val isOcean = themeKey.trim().lowercase(PtBr) == OCEAN_THEME_KEY
            val isForest = themeKey.trim().lowercase(PtBr) == FOREST_THEME_KEY
            val isAurora = themeKey.trim().lowercase(PtBr) == AURORA_THEME_KEY

            RemindrTheme(option ?: midnight ?: ThemeOption.entries.first()) {
                val base = MaterialTheme.colorScheme
                val baseDark = base.background.luminance() < 0.5f
                val wantDark = when {
                    appearance.followSystem -> systemDark
                    appearance.dark != null -> appearance.dark == true
                    option == null -> true
                    else -> baseDark
                }
                val scheme = remember(base, wantDark, option, isMidnight, isOcean, isForest, isAurora) {
                    when {
                        isOcean -> oceanScheme(wantDark)
                        isForest -> forestScheme(wantDark)
                        isAurora -> auroraScheme(wantDark)
                        isMidnight && midnight != null -> {
                            if (wantDark == baseDark) base else deriveScheme(base, wantDark)
                        }
                        option == null && midnight != null -> {
                            if (wantDark == baseDark) base else deriveScheme(base, wantDark)
                        }
                        wantDark == baseDark -> base
                        else -> deriveScheme(base, wantDark)
                    }
                }
                SideEffect {
                    applySystemBarStyle()
                    window.decorView.setBackgroundColor(scheme.background.toArgb())
                    WindowCompat.getInsetsController(window, view)?.let { controller ->
                        controller.isAppearanceLightStatusBars = !wantDark
                        controller.isAppearanceLightNavigationBars = !wantDark
                    }
                }
                MaterialTheme(
                    colorScheme = scheme,
                    typography = MaterialTheme.typography,
                    shapes = MaterialTheme.shapes
                ) {
                    RemindrApp(
                        database = database,
                        security = security,
                        appearance = appearance,
                        themeKey = themeKey,
                        onAppearanceChange = { updated ->
                            appearance = updated
                            saveAppearance(this@MainActivity, updated)
                        },
                        onThemeSelected = { selected ->
                            saveThemeKey(this@MainActivity, selected)
                            themeKey = selected
                        },
                        authenticate = ::authenticateWithBiometrics,
                        biometricAvailable = ::biometricAvailable
                    )
                }
            }
        }
    }

    private fun enableFullScreenLayout() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val params = window.attributes
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }
    }

    private fun applySystemBarStyle() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && buttonNavigation) {
            window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
        }
    }

    private fun usesButtonNavigation(): Boolean {
        val mode = runCatching { Settings.Secure.getInt(contentResolver, "navigation_mode", -1) }.getOrDefault(-1)
        if (mode >= 0) return mode != 2
        val resId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        if (resId > 0) return runCatching { resources.getInteger(resId) != 2 }.getOrDefault(true)
        return true
    }

    private fun biometricAvailable(): Boolean = BiometricManager.from(this).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    ) == BiometricManager.BIOMETRIC_SUCCESS

    private fun authenticateWithBiometrics(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!biometricAvailable()) {
            onError("Nenhuma impressão digital pronta para uso foi encontrada neste dispositivo.")
            return
        }

        BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) = onSuccess()

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    if (
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        return
                    }

                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() =
                    onError("A impressão digital não foi reconhecida.")
            }
        ).authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloquear Remindr")
                .setSubtitle("Confirme sua identidade para continuar")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                )
                .setNegativeButtonText("Cancelar")
                .build()
        )
    }
}

private data class AppearancePrefs(val followSystem: Boolean = false, val dark: Boolean? = null)

private const val FALLBACK_DEFAULT_THEME_KEY = "meia-noite"
private const val OCEAN_THEME_KEY = "oceano"
private const val FOREST_THEME_KEY = "floresta"
private const val LEGACY_DEFAULT_THEME_KEY = "padrao"
private const val LEGACY_OCEAN_DEFAULT_KEY = "oceano"
private const val AURORA_THEME_KEY = "aurora"

private val MidnightThemeNames = setOf(
    "meia-noite", "meia noite", "meianoite", "midnight"
)

private fun ThemeOption.normalizedNames(): Set<String> = setOf(
    label.toString().trim().lowercase(PtBr),
    key.toString().trim().lowercase(PtBr)
)

private fun ThemeOption.isMidnightTheme(): Boolean =
    normalizedNames().any { it in MidnightThemeNames }

private fun midnightThemeOption(): ThemeOption? =
    ThemeOption.entries.firstOrNull { it.isMidnightTheme() }

private fun defaultThemeKey(): String =
    midnightThemeOption()?.key?.toString()?.trim()?.takeIf { it.isNotEmpty() }
        ?: FALLBACK_DEFAULT_THEME_KEY

private fun loadAppearance(context: Context): AppearancePrefs {
    val prefs = context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE)
    return AppearancePrefs(
        followSystem = prefs.getBoolean("follow_system", false),
        dark = if (prefs.contains("dark")) prefs.getBoolean("dark", false) else null
    )
}

private fun saveAppearance(context: Context, value: AppearancePrefs) {
    val editor = context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE).edit()
    editor.putBoolean("follow_system", value.followSystem)
    val dark = value.dark
    if (dark != null) editor.putBoolean("dark", dark) else editor.remove("dark")
    editor.apply()
}

private fun loadThemeKey(context: Context): String {
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

private fun saveThemeKey(context: Context, key: String) {
    context.getSharedPreferences("remindr_appearance", Context.MODE_PRIVATE)
        .edit().putString("theme_key", key).apply()
}

private val HiddenThemeNames = setOf(
    "padrão do sistema", "padrao do sistema", "padrão", "padrao", "sistema", "system", "default",
    "claro", "tema claro", "light", "escuro", "tema escuro", "dark"
)

private fun ThemeOption.isHidden(): Boolean {
    val l = label.toString().trim().lowercase(PtBr)
    val k = key.toString().trim().lowercase(PtBr)
    return l in HiddenThemeNames || k in HiddenThemeNames
}

private fun ThemeOption.isForestTheme(): Boolean = "floresta" in normalizedNames()
private fun ThemeOption.isOceanTheme(): Boolean = "oceano" in normalizedNames()
private fun ThemeOption.isAuroraTheme(): Boolean = AURORA_THEME_KEY in normalizedNames()

private data class ThemeEntry(
    val key: String,
    val label: String,
    val description: String,
    val colors: List<Color>
)

private val OceanEntry = ThemeEntry(
    key = OCEAN_THEME_KEY,
    label = "Oceano",
    description = "Azul oceânico",
    colors = listOf(Color(0xFF071A1C), Color(0xFF1696A1), Color(0xFF73C9D1))
)

private val ForestEntry = ThemeEntry(
    key = FOREST_THEME_KEY,
    label = "Floresta",
    description = "Verde floresta, a identidade do Remindr",
    colors = listOf(Color(0xFF0C100E), Color(0xFF4CC38A), Color(0xFFB4D36A))
)

private val AuroraEntry = ThemeEntry(
    key = AURORA_THEME_KEY,
    label = "Aurora",
    description = "Violeta, turquesa e um toque rosado",
    colors = listOf(Color(0xFF171525), Color(0xFF7562C4), Color(0xFF75D7C1))
)

private fun themeEntries(): List<ThemeEntry> {
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
                .filterNot { it.isHidden() || it.isMidnightTheme() || it.isForestTheme() || it.isOceanTheme() || it.isAuroraTheme() }
                .map {
                    ThemeEntry(
                        it.key.toString(),
                        it.label.toString(),
                        it.description.toString(),
                        it.swatches.toList()
                    )
                }
}

private fun forestScheme(dark: Boolean): ColorScheme = if (dark) {
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

private fun oceanScheme(dark: Boolean): ColorScheme = if (dark) {
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

private fun auroraScheme(dark: Boolean): ColorScheme = if (dark) {
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

private fun deriveScheme(base: ColorScheme, dark: Boolean): ColorScheme {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(base.primary.toArgb(), hsv)
    fun tone(sat: Float, value: Float): Color = Color.hsv(hsv[0], sat, value)
    fun accent(c: Color): Color = when {
        dark && c.luminance() < 0.28f -> lerp(c, Color.White, 0.40f)
        !dark && c.luminance() > 0.45f -> lerp(c, Color.Black, 0.32f)
        else -> c
    }
    fun contentOn(c: Color): Color = if (c.luminance() > 0.5f) Color(0xFF111111) else Color.White

    val primary = accent(base.primary)
    val secondary = accent(base.secondary)
    val tertiary = accent(base.tertiary)
    val bg = if (dark) tone(0.30f, 0.08f) else tone(0.05f, 0.985f)

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
        background = bg,
        onBackground = if (dark) tone(0.06f, 0.95f) else tone(0.30f, 0.10f),
        surface = bg,
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

private sealed interface Screen {
    data object Intro : Screen
    data object Main : Screen
    data object Lock : Screen
    data object Security : Screen
    data object Themes : Screen
    data class Editor(val birthdayId: Long? = null) : Screen
    data class Details(val birthdayId: Long) : Screen
}

enum class MainTab {
    Calendar, Home, Settings
}

private fun MainTab.label(): String = when (this) {
    MainTab.Calendar -> "Calendário"
    MainTab.Home -> "Início"
    MainTab.Settings -> "Ajustes"
}

private fun getTabIcon(tab: MainTab, isSelected: Boolean): ImageVector = when (tab) {
    MainTab.Calendar -> if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
    MainTab.Home -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
    MainTab.Settings -> if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings
}

private data class Popup(
    val title: String,
    val message: String,
    val confirmLabel: String = "Continuar",
    val destructive: Boolean = false,
    val onConfirm: () -> Unit
)

private enum class PinMode { Create, Reset }

private enum class HomeFilter(val label: String) {
    Upcoming("Próximos"),
    Month("Este mês"),
    Alphabetical("A–Z")
}

@Composable
private fun RemindrApp(
    database: RemindrDatabase,
    security: SecurityStore,
    appearance: AppearancePrefs,
    themeKey: String,
    onAppearanceChange: (AppearancePrefs) -> Unit,
    onThemeSelected: (String) -> Unit,
    authenticate: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    biometricAvailable: () -> Boolean
) {
    val context = LocalContext.current
    var birthdays by remember { mutableStateOf(database.birthdays().sortedBy { it.daysUntil() }) }
    var screen by remember {
        mutableStateOf<Screen>(if (!security.introSeen()) Screen.Intro else Screen.Lock)
    }
    var popup by remember { mutableStateOf<Popup?>(null) }
    var pinMode by remember { mutableStateOf<PinMode?>(null) }

    fun refresh() {
        birthdays = database.birthdays().sortedBy { it.daysUntil() }
        ReminderScheduler.scheduleAll(context)
    }

    fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true
        }

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager?.canScheduleExactAlarms() == true
    }

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(
                Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
    }

    fun openAppSettings() {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
        )
    }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (!hasExactAlarmPermission()) {
                openExactAlarmSettings()
            }
        } else {
            popup = Popup(
                title = "Notificações não ativadas",
                message = "Você poderá permitir as notificações nas configurações do Android.",
                confirmLabel = "Entendi"
            ) {}
        }
    }

    val exportBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.write(BackupCodec.encode(database.birthdays()))
            }
        }.onSuccess {
            popup = Popup("Backup exportado", "Seu arquivo do Remindr foi salvo com segurança.", "Concluir") {}
        }
    }

    val importBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) runCatching {
            val imported = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                BackupCodec.decode(reader.readText())
            }.orEmpty()
            if (imported.isEmpty()) error("empty backup")
            imported.forEach { database.insert(it) }
            imported.size
        }.onSuccess { count ->
            refresh()
            popup = Popup("Backup importado", "$count memórias foram adicionadas à sua lista.", "Concluir") {}
        }
    }

    LaunchedEffect(Unit) { ReminderScheduler.scheduleAll(context) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.05f, animationSpec = tween(300))
            },
            label = "ScreenTransitions",
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
        ) { currentScreen ->
            when (currentScreen) {
                Screen.Intro -> IntroScreen(onContinue = {
                    security.setIntroSeen()
                    screen = Screen.Main
                })
                Screen.Lock -> LockScreen(
                    hasPin = security.hasPin,
                    biometricEnabled = security.biometricEnabled,
                    onPin = { pin ->
                        if (security.verifies(pin)) screen = Screen.Main
                        else popup = Popup("PIN incorreto", "Tente novamente ou use a biometria.", "Tentar de novo") {}
                    },
                    onBiometric = {
                        authenticate({ screen = Screen.Main }) { error ->
                            popup = Popup("Erro biométrico", error, "Entendi") {}
                        }
                    },
                    onForgot = {
                        authenticate({ pinMode = PinMode.Reset }) { error ->
                            popup = Popup("Recuperação indisponível", error, "Entendi") {}
                        }
                    },
                    onEnter = { screen = Screen.Main }
                )
                Screen.Main -> MainContainer(
                    birthdays = birthdays,
                    onAdd = { screen = Screen.Editor() },
                    onOpen = { screen = Screen.Details(it.id) },
                    onSecurity = { screen = Screen.Security },
                    onThemes = { screen = Screen.Themes },
                    onImport = { importBackup.launch(arrayOf("text/plain")) },
                    onExport = { exportBackup.launch("remindr_backup.txt") },
                    onEnableNotifications = {
                        when {
                            !hasNotificationPermission() -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermission.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                } else if (!hasExactAlarmPermission()) {
                                    openExactAlarmSettings()
                                } else {
                                    openAppSettings()
                                }
                            }

                            !hasExactAlarmPermission() -> {
                                openExactAlarmSettings()
                            }

                            else -> {
                                openAppSettings()
                            }
                        }
                    }
                )
                Screen.Security -> SecurityScreen(
                    hasPin = security.hasPin,
                    biometricEnabled = security.biometricEnabled,
                    onBack = { screen = Screen.Main },
                    onCreatePin = { pinMode = PinMode.Create },
                    onRemovePin = {
                        popup = Popup("Remover PIN", "O aplicativo deixará de pedir o PIN ao abrir.", "Remover", destructive = true) {
                            security.clearPin()
                        }
                    },
                    onBiometricChange = { enabled ->
                        if (enabled && !biometricAvailable()) {
                            popup = Popup("Biometria indisponível", "Cadastre uma impressão digital no dispositivo antes.", "Entendi") {}
                        } else {
                            security.setBiometricEnabled(enabled)
                            screen = Screen.Main
                        }
                    }
                )
                Screen.Themes -> ThemesScreen(
                    selectedKey = themeKey,
                    appearance = appearance,
                    isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f,
                    onAppearanceChange = onAppearanceChange,
                    onBack = { screen = Screen.Main },
                    onSelect = onThemeSelected
                )
                is Screen.Editor -> EditorScreen(
                    birthday = currentScreen.birthdayId?.let(database::birthday),
                    onBack = { screen = if (currentScreen.birthdayId == null) Screen.Main else Screen.Details(currentScreen.birthdayId) },
                    onSave = { birthday ->
                        val saved = if (birthday.id == 0L) database.insert(birthday).let { birthday.copy(id = it) } else {
                            database.update(birthday)
                            birthday
                        }
                        ReminderScheduler.schedule(context, saved)
                        refresh()
                        screen = Screen.Details(saved.id)
                    }
                )
                is Screen.Details -> database.birthday(currentScreen.birthdayId)?.let { birthday ->
                    DetailsScreen(
                        birthday = birthday,
                        onBack = { screen = Screen.Main },
                        onEdit = { screen = Screen.Editor(birthday.id) },
                        onDelete = {
                            popup = Popup("Remover registro", "Deseja remover ${birthday.name} para sempre?", "Excluir", destructive = true) {
                                database.delete(birthday.id)
                                ReminderScheduler.cancel(context, birthday.id)
                                refresh()
                                screen = Screen.Main
                            }
                        }
                    )
                } ?: run { screen = Screen.Main }
            }
        }
    }

    popup?.let { item -> RemindrDialog(item, onDismiss = { popup = null }) }
    pinMode?.let { mode -> PinDialog(
        mode = mode,
        onDismiss = { pinMode = null },
        onSaved = { pin ->
            security.setPin(pin)
            pinMode = null
            popup = Popup("PIN salvo", "A proteção está ativada com sucesso.", "Concluir") {}
            if (mode == PinMode.Reset) screen = Screen.Main
        }
    ) }
}

private val PtBr: Locale = Locale("pt", "BR")

private val CardShape = RoundedCornerShape(24.dp)
private val FieldShape = RoundedCornerShape(20.dp)
private val TileShape = RoundedCornerShape(14.dp)
private val AvatarShape = RoundedCornerShape(percent = 30)

private fun birthdayDateInYear(date: LocalDate, year: Int): LocalDate {
    val day = minOf(
        date.dayOfMonth,
        date.month.length(Year.of(year).isLeap)
    )

    return LocalDate.of(year, date.month, day)
}

private fun nextOccurrence(date: LocalDate): LocalDate {
    val today = LocalDate.now()
    val thisYear = birthdayDateInYear(date, today.year)

    return if (thisYear.isBefore(today)) {
        birthdayDateInYear(date, today.year + 1)
    } else {
        thisYear
    }
}

private fun Birthday.upcomingDate(): LocalDate = nextOccurrence(birthDate)

private fun Birthday.upcomingAge(): Int = upcomingDate().year - birthDate.year

private fun yearsWord(age: Int): String = if (age == 1) "ano" else "anos"

private fun shortDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("d 'de' MMM", PtBr)).replace(".", "")

private fun rowSubtitle(b: Birthday, withDate: Boolean): String {
    val age = b.upcomingAge()
    val date = shortDate(b.upcomingDate())
    return when {
        age <= 0 -> date
        !withDate -> "Faz $age ${yearsWord(age)}"
        b.daysUntil() == 0L -> "Faz $age ${yearsWord(age)} hoje"
        else -> "Faz $age ${yearsWord(age)} em $date"
    }
}

private fun groupLabel(days: Long): String = when {
    days == 0L -> "Hoje"
    days <= 7L -> "Próximos 7 dias"
    days <= 30L -> "Próximos 30 dias"
    days <= 90L -> "Próximos 3 meses"
    else -> "Mais adiante"
}

private fun avatarColors(name: String): Pair<Color, Color> {
    val first = name.trim().firstOrNull()?.uppercaseChar() ?: '?'
    val h1 = (first.code * 137.508f) % 360f
    val h2 = (h1 + 40f) % 360f
    return Color.hsv(h1, 0.55f, 0.92f) to Color.hsv(h2, 0.75f, 0.72f)
}

private fun monthCells(month: YearMonth): List<Int?> {
    val lead = month.atDay(1).dayOfWeek.value % 7
    val days = (1..month.lengthOfMonth()).toList()
    val base: List<Int?> = List<Int?>(lead) { null } + days
    val pad = (7 - base.size % 7) % 7
    return base + List<Int?>(pad) { null }
}

@Composable
private fun hairline(): Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

@Composable
private fun topInset(): Dp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

@Composable
private fun bottomInset(): Dp =
    WindowInsets.navigationBars.exclude(WindowInsets.ime).asPaddingValues().calculateBottomPadding()

@Composable
private fun brandBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(cs.primary, lerp(cs.primary, cs.tertiary, 0.45f)))
}

@Composable
private fun BrandWordmark(width: Dp, height: Dp, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val isDark = cs.background.luminance() < 0.5f
    Image(
        painter = painterResource(id = R.drawable.title_white),
        contentDescription = "Remindr",
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(if (isDark) Color.White else Color.Black),
        modifier = modifier
            .width(width)
            .height(height)
    )
}

@Composable
private fun BrandAccentBar() {
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(brandBrush())
    )
}

@Composable
private fun BrandAmbient(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val top = topInset()
    val bottom = bottomInset()
    val infinite = rememberInfiniteTransition(label = "ambient")
    val glow by infinite.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp + top)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            cs.primary.copy(alpha = 0.12f),
                            cs.tertiary.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(520.dp)
                .offset(y = (-190).dp + top)
                .background(
                    Brush.radialGradient(
                        listOf(
                            cs.primary.copy(alpha = glow),
                            cs.tertiary.copy(alpha = glow * 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(300.dp + bottom)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, cs.primary.copy(alpha = 0.10f))
                    )
                )
        )
    }
}

@Composable
private fun Reveal(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(durationMillis = 560, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "reveal"
    )
    Box(
        modifier = modifier
            .alpha(progress)
            .offset(y = ((1f - progress) * 24f).dp)
    ) { content() }
}

@Composable
private fun dialogContainer(): Color {
    val cs = MaterialTheme.colorScheme
    return if (cs.background.luminance() < 0.5f) cs.surfaceContainerHigh else Color.White
}

@Composable
private fun dialogBorder(): Color {
    val cs = MaterialTheme.colorScheme
    return if (cs.background.luminance() < 0.5f) hairline() else cs.outlineVariant
}

@Composable
private fun SurfaceCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = shape,
        color = cs.surfaceContainerLow,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
    fontSize: TextUnit = 16.sp
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = cs.primary,
        contentColor = cs.onPrimary,
        modifier = modifier.height(height)
    ) {
        Row(
            modifier = Modifier.background(brandBrush()).padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, null, tint = cs.onPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(label, color = cs.onPrimary, fontSize = fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun PersonAvatar(name: String, size: Dp, modifier: Modifier = Modifier, round: Boolean = false) {
    val colors = avatarColors(name)
    val shape: Shape = if (round) CircleShape else AvatarShape
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.first, colors.second))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().take(1).uppercase().ifEmpty { "?" },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.4f).sp
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = cs.surfaceContainerHigh,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, description, tint = cs.onSurface, modifier = Modifier.size(iconSize))
        }
    }
}

@Composable
private fun SoftPill(label: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(onClick = onClick, shape = CircleShape, color = cs.primary.copy(alpha = 0.14f)) {
        Text(
            text = label,
            color = cs.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun IconTile(icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(TileShape)
            .background(accent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ScreenTopBar(title: String, onBack: () -> Unit, trailing: @Composable () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            description = "Voltar",
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = title,
            color = cs.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(Modifier.align(Alignment.CenterEnd)) { trailing() }
    }
}

@Composable
private fun LargeTitle(title: String, subtitle: String? = null, trailing: @Composable () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = cs.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = cs.onSurfaceVariant,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        trailing()
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .navigationBarsPadding()
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(AvatarShape)
                .background(cs.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = cs.primary, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            color = cs.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.3).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = cs.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null) {
            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                label = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Add,
                height = 54.dp
            )
        }
        if (secondaryLabel != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onSecondary) {
                Text(secondaryLabel, color = cs.primary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun MainContainer(
    birthdays: List<Birthday>,
    onAdd: () -> Unit,
    onOpen: (Birthday) -> Unit,
    onSecurity: () -> Unit,
    onThemes: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onEnableNotifications: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val navBottom = bottomInset()
    var currentTab by remember { mutableStateOf(MainTab.Home) }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
            label = "TabTransitions",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                MainTab.Calendar -> CalendarScreen(birthdays, onOpen)
                MainTab.Home -> HomeScreen(birthdays, onAdd, onOpen, onEnableNotifications)
                MainTab.Settings -> SettingsScreen(birthdays.size, onSecurity, onThemes, onImport, onExport, onEnableNotifications)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp + navBottom)
                .background(Brush.verticalGradient(listOf(Color.Transparent, cs.background)))
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingTabBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it },
                modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(
                visible = currentTab != MainTab.Settings,
                enter = fadeIn(tween(200)) + expandHorizontally(tween(250)),
                exit = fadeOut(tween(150)) + shrinkHorizontally(tween(200))
            ) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        onClick = onAdd,
                        shape = CircleShape,
                        color = cs.primary,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(
                            modifier = Modifier.background(brandBrush()),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Add, "Adicionar aniversário", tint = cs.onPrimary, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingTabBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        shape = CircleShape,
        color = cs.surfaceContainerHigh,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.height(62.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val tabWeight by animateFloatAsState(
                    targetValue = if (isSelected) 2.3f else 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                    label = "tabWeight"
                )
                val pill by animateColorAsState(
                    targetValue = if (isSelected) cs.primary.copy(alpha = 0.16f) else Color.Transparent,
                    animationSpec = tween(220),
                    label = "tabPill"
                )
                val tint by animateColorAsState(
                    targetValue = if (isSelected) cs.primary else cs.onSurfaceVariant.copy(alpha = 0.75f),
                    animationSpec = tween(220),
                    label = "tabTint"
                )
                Row(
                    modifier = Modifier
                        .weight(tabWeight)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(pill)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getTabIcon(tab, isSelected),
                        contentDescription = tab.label(),
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(220, delayMillis = 60)) + expandHorizontally(tween(260)),
                        exit = fadeOut(tween(100)) + shrinkHorizontally(tween(200))
                    ) {
                        Text(
                            text = tab.label(),
                            color = tint,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    birthdays: List<Birthday>,
    onAdd: () -> Unit,
    onOpen: (Birthday) -> Unit,
    onEnableNotifications: () -> Unit
) {
    var filter by remember { mutableStateOf(HomeFilter.Upcoming) }
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val navBottom = bottomInset()

    LaunchedEffect(searching) {
        if (searching) {
            delay(180)
            runCatching { focus.requestFocus() }
        }
    }

    val visible = remember(birthdays, filter, query) {
        val q = query.trim()
        val base = if (q.isEmpty()) birthdays else birthdays.filter { it.name.contains(q, ignoreCase = true) }
        when (filter) {
            HomeFilter.Upcoming -> base.sortedBy { it.daysUntil() }
            HomeFilter.Month -> base
                .filter { it.birthDate.monthValue == LocalDate.now().monthValue }
                .sortedBy { it.birthDate.dayOfMonth }
            HomeFilter.Alphabetical -> base.sortedBy { it.name.lowercase() }
        }
    }

    val hero = if (filter == HomeFilter.Upcoming && query.isBlank() && visible.isNotEmpty()) visible.first() else null
    val rest = if (hero != null) visible.drop(1) else visible

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).height(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                this@Row.AnimatedVisibility(
                    visible = !searching,
                    enter = fadeIn(tween(260, delayMillis = 120)) + slideInHorizontally(tween(320)) { -it / 4 },
                    exit = fadeOut(tween(160)) + slideOutHorizontally(tween(260)) { -it / 4 }
                ) {
                    BrandTitle(count = birthdays.size)
                }
                this@Row.AnimatedVisibility(
                    visible = searching,
                    enter = expandHorizontally(
                        animationSpec = tween(340, easing = FastOutSlowInEasing),
                        expandFrom = Alignment.End
                    ) + fadeIn(tween(220)),
                    exit = shrinkHorizontally(
                        animationSpec = tween(260, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.End
                    ) + fadeOut(tween(160))
                ) {
                    SearchPill(
                        query = query,
                        onQueryChange = { query = it },
                        focusRequester = focus,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (birthdays.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                CircleIconButton(
                    icon = if (searching) Icons.Outlined.Close else Icons.Outlined.Search,
                    description = if (searching) "Fechar busca" else "Buscar",
                    onClick = {
                        if (searching) {
                            focusManager.clearFocus()
                            query = ""
                        }
                        searching = !searching
                    }
                )
            }
        }

        if (birthdays.isNotEmpty()) {
            SegmentedControl(
                options = HomeFilter.entries.map { it.label },
                selectedIndex = filter.ordinal,
                onSelect = { filter = HomeFilter.entries[it] },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        when {
            birthdays.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Celebration,
                title = "Nenhum aniversário ainda",
                message = "Adicione o primeiro e o Remindr avisa você no dia de cada comemoração.",
                modifier = Modifier.weight(1f),
                actionLabel = "Adicionar aniversário",
                onAction = onAdd,
                secondaryLabel = "Ativar lembretes",
                onSecondary = onEnableNotifications
            )
            visible.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Search,
                title = "Nada por aqui",
                message = if (query.isNotBlank()) "Nenhum nome parecido com \"${query.trim()}\"." else "Ninguém faz aniversário neste mês.",
                modifier = Modifier.weight(1f)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 150.dp + navBottom)
            ) {
                if (hero != null) {
                    item(key = "hero") { HeroCard(hero) { onOpen(hero) } }
                }
                if (filter == HomeFilter.Upcoming) {
                    val groups = rest.groupBy { groupLabel(it.daysUntil()) }
                    groups.forEach { (label, list) ->
                        item(key = "group_$label") { GroupHeader(label, list.size) }
                        itemsIndexed(list, key = { _, b -> b.id }) { index, b ->
                            BirthdayRow(b, rowSubtitle(b, true), index < list.lastIndex) { onOpen(b) }
                        }
                    }
                } else {
                    itemsIndexed(rest, key = { _, b -> b.id }) { index, b ->
                        BirthdayRow(b, rowSubtitle(b, true), index < rest.lastIndex) { onOpen(b) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandTitle(count: Int) {
    BrandWordmark(width = 180.dp, height = 36.dp)
}

@Composable
private fun HeroCard(birthday: Birthday, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val isDark = cs.background.luminance() < 0.5f
    val days = birthday.daysUntil()

    val containerBrush = if (isDark) {
        Brush.linearGradient(listOf(lerp(cs.primary, Color.Black, 0.78f), lerp(cs.primary, Color.Black, 0.52f)))
    } else {
        Brush.linearGradient(listOf(lerp(cs.primary, Color.White, 0.88f), lerp(cs.primary, Color.White, 0.70f)))
    }
    val nameColor = if (isDark) Color.White else lerp(cs.primary, Color.Black, 0.72f)
    val accent = if (isDark) lerp(cs.primary, Color.White, 0.55f) else lerp(cs.primary, Color.Black, 0.10f)
    val dateColor = if (isDark) Color.White.copy(alpha = 0.65f) else lerp(cs.primary, Color.Black, 0.55f).copy(alpha = 0.80f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.08f) else cs.primary.copy(alpha = 0.22f)
    val frameColor = if (isDark) Color.White.copy(alpha = 0.92f) else Color.White
    val chevronColor = if (isDark) Color.White.copy(alpha = 0.6f) else nameColor.copy(alpha = 0.6f)

    val parts = birthday.name.trim().split(Regex("\\s+"), limit = 2)
    val firstName = parts.first()
    val surname = parts.getOrNull(1).orEmpty()
    val nameText = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(firstName.uppercase(PtBr)) }
        if (surname.isNotEmpty()) {
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append(surname.uppercase(PtBr)) }
        }
    }
    val timeLeft = when (days) {
        0L -> "É hoje!"
        1L -> "É amanhã"
        else -> "Faltam $days dias"
    }
    val timeText = buildAnnotatedString {
        withStyle(SpanStyle(color = accent, fontWeight = FontWeight.SemiBold)) { append(timeLeft) }
        withStyle(SpanStyle(color = dateColor)) {
            append("  ·  " + shortDate(birthday.upcomingDate()))
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(containerBrush)
            .border(1.dp, borderColor, CardShape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(AvatarShape)
                    .background(frameColor)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                PersonAvatar(name = birthday.name, size = 86.dp)
            }
            Spacer(Modifier.width(18.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nameText,
                    color = nameColor,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    letterSpacing = 1.2.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = timeText,
                    fontSize = 14.sp,
                    letterSpacing = 0.3.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = chevronColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun GroupHeader(label: String, count: Int) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = cs.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
            modifier = Modifier.weight(1f)
        )
        Surface(shape = CircleShape, color = cs.surfaceContainerHigh) {
            Text(
                text = count.toString(),
                color = cs.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun BirthdayRow(
    birthday: Birthday,
    subtitle: String,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonAvatar(name = birthday.name, size = 54.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = birthday.name,
                    color = cs.onBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            DaysPill(birthday.daysUntil())
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 88.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = hairline()
            )
        }
    }
}

@Composable
private fun DaysPill(days: Long) {
    val cs = MaterialTheme.colorScheme
    val label = when (days) {
        0L -> "Hoje"
        1L -> "Amanhã"
        else -> "$days dias"
    }
    val container = when {
        days == 0L -> cs.primary
        days <= 7L -> cs.primary.copy(alpha = 0.16f)
        else -> cs.surfaceContainerHigh
    }
    val content = when {
        days == 0L -> cs.onPrimary
        days <= 7L -> cs.primary
        else -> cs.onSurfaceVariant
    }
    Surface(shape = CircleShape, color = container) {
        Text(
            text = label,
            color = content,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val thumbBrush = brandBrush()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(cs.surfaceContainerLow)
            .border(1.dp, hairline(), CircleShape)
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / options.size
        val thumbOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "segmentThumb"
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(thumbBrush)
        )
        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val textColor by animateColorAsState(
                    targetValue = if (selected) cs.onPrimary else cs.onSurfaceVariant,
                    animationSpec = tween(180),
                    label = "segmentText"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelect(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(cs.surfaceContainerHigh)
            .border(1.dp, hairline(), CircleShape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, null, tint = cs.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text("Buscar por nome", color = cs.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 16.sp, maxLines = 1, softWrap = false)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = cs.onSurface, fontSize = 16.sp),
                cursorBrush = SolidColor(cs.primary),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }
    }
}

@Composable
private fun CalendarScreen(birthdays: List<Birthday>, onOpen: (Birthday) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val today = remember { LocalDate.now() }
    val byDay = remember(birthdays, currentMonth.year) {
        birthdays.groupBy {
            val day = minOf(
                it.birthDate.dayOfMonth,
                it.birthDate.month.length(Year.of(currentMonth.year).isLeap)
            )

            it.birthDate.monthValue * 100 + day
        }
    }
    val monthCount = birthdays.count { it.birthDate.monthValue == currentMonth.monthValue }
    val monthName = currentMonth.month.getDisplayName(JTextStyle.FULL, PtBr).replaceFirstChar { it.uppercase() }

    fun goTo(month: YearMonth) {
        currentMonth = month
        selectedDate = if (month == YearMonth.now()) LocalDate.now() else month.atDay(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LargeTitle(title = "Calendário", subtitle = "Visualize melhor os aniversários")

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(CardShape)
                .background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.12f), cs.surfaceContainerLow)))
                .border(1.dp, hairline(), CardShape)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                monthName,
                                color = cs.onSurface,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                currentMonth.year.toString(),
                                color = cs.onSurfaceVariant,
                                fontSize = 21.sp,
                                letterSpacing = (-0.3).sp,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = when (monthCount) {
                                0 -> "Nenhum aniversário neste mês"
                                1 -> "1 aniversário neste mês"
                                else -> "$monthCount aniversários neste mês"
                            },
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (currentMonth != YearMonth.now()) {
                        SoftPill("Hoje") { goTo(YearMonth.now()) }
                        Spacer(Modifier.width(8.dp))
                    }
                    CircleIconButton(
                        icon = Icons.Outlined.ChevronLeft,
                        description = "Mês anterior",
                        onClick = { goTo(currentMonth.minusMonths(1)) },
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                    Spacer(Modifier.width(6.dp))
                    CircleIconButton(
                        icon = Icons.Outlined.ChevronRight,
                        description = "Próximo mês",
                        onClick = { goTo(currentMonth.plusMonths(1)) },
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                }

                Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp)) {
                    listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { d ->
                        Text(
                            text = d,
                            color = cs.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "MonthGrid"
                ) { month ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        monthCells(month).chunked(7).forEach { week ->
                            Row(Modifier.fillMaxWidth()) {
                                week.forEach { day ->
                                    if (day == null) {
                                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                                    } else {
                                        val date = month.atDay(day)
                                        val people = byDay[date.monthValue * 100 + date.dayOfMonth].orEmpty()
                                        CalendarDay(
                                            day = day,
                                            people = people,
                                            selected = date == selectedDate,
                                            isToday = date == today,
                                            modifier = Modifier.weight(1f)
                                        ) { selectedDate = date }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = selectedDate,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
            label = "DayList",
            modifier = Modifier.fillMaxWidth()
        ) { date ->
            val list = byDay[date.monthValue * 100 + date.dayOfMonth].orEmpty()
            val weekdayShort = date.dayOfWeek.getDisplayName(JTextStyle.SHORT, PtBr).removeSuffix(".")
            val title = if (date == today) {
                "Hoje, " + date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", PtBr))
            } else {
                date.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", PtBr)).replaceFirstChar { it.uppercase() }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                DaySummaryCard(
                    date = date,
                    title = title,
                    weekdayShort = weekdayShort,
                    birthdayCount = list.size,
                    accentColors = list.firstOrNull()?.let { avatarColors(it.name) }
                )

                if (list.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    SurfaceCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                        list.forEachIndexed { index, b ->
                            BirthdayRow(b, rowSubtitle(b, false), index < list.lastIndex) { onOpen(b) }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding().height(150.dp))
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    people: List<Birthday>,
    selected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val has = people.isNotEmpty()
    val shape = TileShape
    val accent = people.firstOrNull()?.let { avatarColors(it.name).first }
    val background by animateColorAsState(
        targetValue = when {
            selected && accent != null -> accent
            selected -> cs.primary
            accent != null -> accent.copy(alpha = 0.18f)
            else -> cs.onSurface.copy(alpha = 0.05f)
        },
        animationSpec = tween(180),
        label = "dayBackground"
    )
    val borderModifier = when {
        selected && accent != null -> Modifier
        selected -> Modifier
        isToday -> Modifier.border(1.5.dp, cs.primary, shape)
        accent != null -> Modifier.border(1.dp, accent.copy(alpha = 0.55f), shape)
        else -> Modifier
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(shape)
            .background(background)
            .then(borderModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                color = when {
                    selected -> Color.White
                    accent != null -> accent
                    else -> cs.onSurface
                },
                fontSize = 14.sp,
                fontWeight = if (selected || isToday || has) FontWeight.Bold else FontWeight.Normal
            )
            if (has) {
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    people.take(3).forEach { birthday ->
                        Box(
                            Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) Color.White
                                    else avatarColors(birthday.name).first
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySummaryCard(
    date: LocalDate,
    title: String,
    weekdayShort: String,
    birthdayCount: Int,
    accentColors: Pair<Color, Color>?
) {
    val cs = MaterialTheme.colorScheme
    val summaryBrush = when {
        accentColors != null -> Brush.linearGradient(
            listOf(
                accentColors.first.copy(alpha = 0.22f),
                accentColors.second.copy(alpha = 0.10f),
                cs.surfaceContainerLow
            )
        )
        else -> Brush.linearGradient(
            listOf(cs.primary.copy(alpha = 0.10f), cs.surfaceContainerLow)
        )
    }
    val badgeBrush = if (accentColors != null) {
        Brush.linearGradient(listOf(accentColors.first, accentColors.second))
    } else {
        brandBrush()
    }
    val borderColor = accentColors?.first?.copy(alpha = 0.35f) ?: hairline()

    Surface(
        shape = CardShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(summaryBrush)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(badgeBrush),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = weekdayShort,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    date.dayOfMonth.toString(),
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "DIA SELECIONADO",
                    color = accentColors?.first ?: cs.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
                Text(
                    text = title,
                    color = cs.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = when (birthdayCount) {
                        0 -> "Sem aniversários neste dia"
                        1 -> "1 aniversário"
                        else -> "$birthdayCount aniversários"
                    },
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailsScreen(birthday: Birthday, onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val days = birthday.daysUntil()
    val age = birthday.upcomingAge()
    val weekday = birthday.upcomingDate().dayOfWeek
        .getDisplayName(JTextStyle.SHORT, PtBr)
        .removeSuffix(".")
        .replaceFirstChar { it.uppercase() }
    val statusText = when (days) {
        0L -> "Hoje é o aniversário"
        1L -> "É amanhã"
        else -> "Faltam $days dias"
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(
            title = "",
            onBack = onBack,
            trailing = { CircleIconButton(Icons.Outlined.Edit, "Editar", onEdit) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            PersonAvatar(name = birthday.name, size = 112.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = birthday.name,
                color = cs.onBackground,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = birthday.birthDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PtBr)),
                color = cs.onSurfaceVariant,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(16.dp))
            Surface(shape = CircleShape, color = if (days == 0L) cs.primary else cs.primary.copy(alpha = 0.14f)) {
                Text(
                    text = statusText,
                    color = if (days == 0L) cs.onPrimary else cs.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                )
            }

            Spacer(Modifier.height(28.dp))
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatCell("Faltam", days.toString(), if (days == 1L) "dia" else "dias", Modifier.weight(1f))
                    Box(Modifier.width(0.5.dp).height(36.dp).background(hairline()))
                    StatCell("Completa", if (age > 0) age.toString() else "—", if (age > 0) yearsWord(age) else "", Modifier.weight(1f))
                    Box(Modifier.width(0.5.dp).height(36.dp).background(hairline()))
                    StatCell("Cai em", weekday, "", Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(16.dp))
            SurfaceCard {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("Anotações", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    if (birthday.notes.isBlank()) {
                        Text(
                            text = "Nada por aqui ainda. Toque em editar para guardar ideias de presente.",
                            color = cs.onSurfaceVariant,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    } else {
                        Text(
                            text = birthday.notes,
                            color = cs.onSurface.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Surface(
                onClick = onDelete,
                shape = CircleShape,
                color = cs.error.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, cs.error.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Delete, null, tint = cs.error, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Apagar registro", color = cs.error, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.navigationBarsPadding().height(32.dp))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = cs.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = cs.onSurface, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    color = cs.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 3.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EditorScreen(birthday: Birthday?, onBack: () -> Unit, onSave: (Birthday) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var name by remember(birthday) { mutableStateOf(birthday?.name.orEmpty()) }
    var notes by remember(birthday) { mutableStateOf(birthday?.notes.orEmpty()) }
    var date by remember(birthday) { mutableStateOf(birthday?.birthDate) }
    val today = LocalDate.now()
    var calendarOpen by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf(false) }
    var nameFocused by remember { mutableStateOf(false) }
    var notesFocused by remember { mutableStateOf(false) }

    val nameInvalid = validationError && name.isBlank()
    val dateInvalid = validationError &&
            (date == null || date?.isAfter(today) == true)
    val hint = date?.let { d ->
        when (val n = ChronoUnit.DAYS.between(LocalDate.now(), nextOccurrence(d))) {
            0L -> "O aniversário é hoje"
            1L -> "O aniversário é amanhã"
            else -> "Faltam $n dias para o aniversário"
        }
    } ?: "Preencha os dados abaixo"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .imePadding()
    ) {
        ScreenTopBar(title = if (birthday == null) "Novo aniversariante" else "Editar registro", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.height(8.dp))
            if (name.isNotBlank()) {
                PersonAvatar(name = name, size = 108.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(AvatarShape)
                        .background(cs.surfaceContainerHigh)
                        .border(1.dp, hairline(), AvatarShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, null, tint = cs.onSurfaceVariant, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = CircleShape, color = cs.primary.copy(alpha = 0.12f)) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(hint, color = cs.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
            FieldCard(label = "Nome", icon = Icons.Outlined.Person, focused = nameFocused, error = nameInvalid) {
                Box(Modifier.fillMaxWidth()) {
                    if (name.isEmpty()) {
                        Text("Nome completo", color = cs.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 18.sp)
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it; validationError = false },
                        singleLine = true,
                        textStyle = TextStyle(color = cs.onSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                        cursorBrush = SolidColor(cs.primary),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { nameFocused = it.isFocused }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            FieldCard(
                label = "Data de nascimento",
                icon = Icons.Outlined.Event,
                focused = false,
                error = dateInvalid,
                modifier = Modifier.clip(FieldShape).clickable { calendarOpen = true }
            ) {
                Text(
                    text = date?.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PtBr)) ?: "Selecionar data",
                    color = if (date != null) cs.onSurface else cs.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))
            FieldCard(label = "Anotações", icon = Icons.Outlined.Edit, focused = notesFocused, error = false) {
                Box(Modifier.fillMaxWidth()) {
                    if (notes.isEmpty()) {
                        Text(
                            "Ideias de presente, gostos, lembretes…",
                            color = cs.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                    BasicTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        minLines = 3,
                        textStyle = TextStyle(color = cs.onSurface, fontSize = 16.sp, lineHeight = 24.sp),
                        cursorBrush = SolidColor(cs.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { notesFocused = it.isFocused }
                    )
                }
            }

            if (nameInvalid || dateInvalid) {
                Text(
                    text = when {
                        nameInvalid && dateInvalid -> "Informe o nome e a data de nascimento."
                        nameInvalid -> "Informe o nome."
                        else -> "Escolha a data de nascimento."
                    },
                    color = cs.error,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 12.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding()
        ) {
            PrimaryButton(
                label = if (birthday == null) "Adicionar aniversariante" else "Salvar alterações",
                onClick = {
                    val invalidDate = date == null || date?.isAfter(today) == true

                    if (name.isBlank() || invalidDate) {
                        validationError = true
                    } else {
                        onSave(
                            Birthday(
                                birthday?.id ?: 0L,
                                name.trim(),
                                date!!,
                                notes.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                fontSize = 17.sp
            )
        }
    }

    if (calendarOpen) {
        val selectableDates = remember(today) {
            object : SelectableDates {

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedDate = Instant
                        .ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    return !selectedDate.isAfter(today)
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= today.year
                }
            }
        }
        val picker = rememberDatePickerState(
            initialSelectedDateMillis = date
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            selectableDates = selectableDates
        )
        val pickerColors = DatePickerDefaults.colors(containerColor = dialogContainer())
        DatePickerDialog(
            onDismissRequest = { calendarOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        picker.selectedDateMillis?.let {
                            val selectedDate = Instant
                                .ofEpochMilli(it)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            if (!selectedDate.isAfter(today)) {
                                date = selectedDate
                                validationError = false
                                calendarOpen = false
                            } else {
                                validationError = true
                            }
                        } ?: run {
                            calendarOpen = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { calendarOpen = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = cs.onSurfaceVariant)
                ) { Text("Cancelar") }
            },
            colors = pickerColors
        ) {
            DatePicker(
                state = picker,
                colors = pickerColors,
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun FieldCard(
    label: String,
    icon: ImageVector,
    focused: Boolean,
    error: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val idleBorder = hairline()
    val borderColor by animateColorAsState(
        targetValue = when {
            error -> cs.error
            focused -> cs.primary
            else -> idleBorder
        },
        animationSpec = tween(160),
        label = "fieldBorder"
    )
    Surface(
        shape = FieldShape,
        color = cs.surfaceContainerLow,
        border = BorderStroke(if (focused || error) 1.5.dp else 1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            IconTile(icon, if (error) cs.error else cs.primary)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = if (error) cs.error else cs.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                content()
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    count: Int,
    onSecurity: () -> Unit,
    onThemes: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onNotifications: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LargeTitle(title = "Ajustes", subtitle = "Personalize o Remindr do seu jeito")

        Column(Modifier.padding(horizontal = 20.dp)) {
            SectionTitle("Geral")
            SurfaceCard {
                SettingsItem(Icons.Outlined.Security, cs.primary, "Privacidade", "PIN e biometria", true, onSecurity)
                SettingsItem(Icons.Outlined.Palette, cs.tertiary, "Aparência", "Modo claro, escuro e cores", true, onThemes)
                SettingsItem(
                    Icons.Outlined.NotificationsActive, cs.secondary, "Avisos",
                    "Lembretes no dia de cada aniversário", false, onNotifications, badge = "Ativar"
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("Backup e dados")
            SurfaceCard {
                SettingsItem(Icons.Outlined.Upload, cs.primary, "Importar", "Restaurar de um arquivo .txt", true, onImport)
                SettingsItem(Icons.Outlined.Download, cs.tertiary, "Exportar", "Salvar um arquivo de backup", false, onExport)
            }

            Spacer(Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandWordmark(width = 120.dp, height = 26.dp, modifier = Modifier.alpha(0.5f))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Versão 1.0.0",
                        color = cs.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding().height(150.dp))
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    showDivider: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val cs = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon, accent)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = cs.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 1.dp))
            }
            if (badge != null) {
                Spacer(Modifier.width(8.dp))
                Surface(shape = CircleShape, color = cs.primary.copy(alpha = 0.14f)) {
                    Text(
                        text = badge,
                        color = cs.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = hairline()
            )
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, accent: Color, title: String, subtitle: String) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconTile(icon, accent)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = cs.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                color = cs.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
private fun FeatureDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 58.dp, top = 14.dp, bottom = 14.dp),
        thickness = 1.dp,
        color = hairline()
    )
}

@Composable
private fun IntroScreen(onContinue: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var shown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { shown = true }

    val headerReveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "introHeaderReveal"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        val roomy = (maxHeight - topInset() - bottomInset()) >= 700.dp

        BrandAmbient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(if (roomy) 40.dp else 20.dp))

            Column(
                modifier = Modifier
                    .alpha(headerReveal)
                    .scale(0.94f + 0.06f * headerReveal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandWordmark(width = 210.dp, height = 48.dp)
                Spacer(Modifier.height(12.dp))
                BrandAccentBar()
            }

            Spacer(Modifier.height(if (roomy) 32.dp else 18.dp))

            Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 140) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bem-vindo ao Remindr!",
                        color = cs.onBackground,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.8).sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Nunca mais esqueça um dia importante",
                        color = cs.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.weight(0.6f))

            Column(Modifier.fillMaxWidth()) {
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 260) {
                    FeatureRow(Icons.Outlined.NotificationsActive, cs.primary, "Lembretes no dia certo", "Uma notificação de cada aniversário.")
                }
                FeatureDivider()
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 500) {
                    FeatureRow(Icons.Outlined.CalendarMonth, cs.secondary, "Calendário completo", "Todas as datas do ano em um só lugar.")
                }
                FeatureDivider()
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 380) {
                    FeatureRow(Icons.Outlined.Lock, cs.tertiary, "Protegido", "Ninguém poderá ver suas anotações ou dados")
                }
            }

            Spacer(Modifier.weight(1f))

            Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 620) {
                PrimaryButton(
                    label = "Começar",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    height = 58.dp,
                    fontSize = 17.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LockWordmarkBlock(reveal: Float, showBar: Boolean) {
    Column(
        modifier = Modifier
            .alpha(reveal)
            .scale(0.94f + 0.06f * reveal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BrandWordmark(width = 200.dp, height = 44.dp)
        if (showBar) {
            Spacer(Modifier.height(12.dp))
            BrandAccentBar()
        }
    }
}

@Composable
private fun LockScreen(
    hasPin: Boolean,
    biometricEnabled: Boolean,
    onPin: (String) -> Unit,
    onBiometric: () -> Unit,
    onForgot: () -> Unit,
    onEnter: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var pin by remember { mutableStateOf("") }
    var shown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { shown = true }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(200)
            onPin(pin)
            pin = ""
        }
    }

    val headerReveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "lockHeaderReveal"
    )
    val reveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "lockReveal"
    )

    val unprotected = !hasPin && !biometricEnabled
    val subtitle = when {
        hasPin && biometricEnabled -> "Digite seu PIN ou use a digital"
        hasPin -> "Digite seu PIN para continuar"
        biometricEnabled -> "Use sua digital para continuar"
        else -> "Nunca mais esqueça o aniversário de alguém importante."
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        val roomy = (maxHeight - topInset() - bottomInset()) >= 720.dp

        BrandAmbient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasPin) {
                Spacer(Modifier.height(if (roomy) 28.dp else 16.dp))
                LockWordmarkBlock(reveal = headerReveal, showBar = roomy)
                Spacer(Modifier.height(if (roomy) 24.dp else 12.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .alpha(reveal)
                    .offset(y = ((1f - reveal) * 28f).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (unprotected) {
                    Spacer(Modifier.weight(1f))
                    LockWordmarkBlock(reveal = headerReveal, showBar = true)
                    Spacer(Modifier.height(24.dp))
                    LockTitle(subtitle)
                    Spacer(Modifier.height(28.dp))
                    SurfaceCard {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconTile(Icons.Outlined.Security, cs.primary)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Proteja seus aniversários",
                                    color = cs.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Ative um PIN ou a digital em Ajustes › Privacidade.",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    PrimaryButton(
                        label = "Entrar",
                        onClick = onEnter,
                        modifier = Modifier.fillMaxWidth(),
                        height = 56.dp,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.height(20.dp))
                } else if (hasPin) {
                    if (roomy) {
                        Spacer(Modifier.height(18.dp))
                    }
                    LockTitle(subtitle)
                    Spacer(Modifier.height(if (roomy) 22.dp else 16.dp))
                    PinBoxes(filled = pin.length, enabled = true)
                    Spacer(Modifier.height(14.dp))
                    if (!biometricEnabled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Esqueceu o PIN? ", color = cs.onSurfaceVariant, fontSize = 14.sp)
                            Text(
                                text = "Redefinir",
                                color = cs.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable(onClick = onForgot).padding(4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    FlatNumpad(
                        onNumber = { if (pin.length < 4) pin += it },
                        onDelete = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                        modifier = Modifier.offset(y = 12.dp),
                        leftSlot = {
                            if (biometricEnabled) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable(onClick = onBiometric),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Fingerprint, "Usar digital", tint = cs.primary, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                } else {
                    Spacer(Modifier.weight(1f))
                    LockWordmarkBlock(reveal = headerReveal, showBar = true)
                    Spacer(Modifier.height(24.dp))
                    LockTitle(subtitle)
                    Spacer(Modifier.height(32.dp))
                    Surface(
                        onClick = onBiometric,
                        shape = CardShape,
                        color = cs.surfaceContainerLow,
                        border = BorderStroke(1.dp, hairline()),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(AvatarShape)
                                    .background(brandBrush()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Fingerprint, "Usar digital", tint = cs.onPrimary, modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Desbloquear com a digital",
                                    color = cs.onSurface,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Toque para autenticar",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                null,
                                tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Lock,
                            null,
                            tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Seus dados ficam só neste aparelho",
                            color = cs.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun LockTitle(subtitle: String) {
    val cs = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Bem-vindo de volta",
            color = cs.onBackground,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = cs.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PinBoxes(filled: Int, enabled: Boolean, box: Dp = 58.dp, gap: Dp = 14.dp) {
    val cs = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)
    val idleBorder = hairline()
    Row(
        horizontalArrangement = Arrangement.spacedBy(gap),
        modifier = Modifier.alpha(if (enabled) 1f else 0.45f)
    ) {
        for (i in 0 until 4) {
            val on = i < filled
            val active = enabled && i == filled
            val bg by animateColorAsState(
                targetValue = if (on) cs.primary else cs.surfaceContainerHigh,
                animationSpec = tween(150),
                label = "pinBoxBg"
            )
            val boxScale by animateFloatAsState(
                targetValue = if (on) 1.06f else 1f,
                animationSpec = spring(dampingRatio = 0.5f),
                label = "pinBoxScale"
            )
            Box(
                modifier = Modifier
                    .size(box)
                    .scale(boxScale)
                    .clip(shape)
                    .background(bg)
                    .border(if (active) 2.dp else 1.dp, if (active) cs.primary else idleBorder, shape),
                contentAlignment = Alignment.Center
            ) {
                if (on) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(cs.onPrimary)
                    )
                }
            }
        }
    }
}

@Composable
private fun FlatNumpad(
    onNumber: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    keyHeight: Dp = 60.dp,
    leftSlot: @Composable () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
    Column(modifier = modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    FlatKey(Modifier.weight(1f), keyHeight, { onNumber(key) }) {
                        Text(key, color = cs.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f).height(keyHeight), contentAlignment = Alignment.Center) { leftSlot() }
            FlatKey(Modifier.weight(1f), keyHeight, { onNumber("0") }) {
                Text("0", color = cs.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium)
            }
            FlatKey(Modifier.weight(1f), keyHeight, onDelete) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    "Apagar",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun FlatKey(modifier: Modifier, height: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun RemindrDialog(popup: Popup, onDismiss: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogContainer(),
            border = BorderStroke(1.dp, dialogBorder()),
            modifier = Modifier.padding(horizontal = 28.dp).fillMaxWidth().widthIn(max = 420.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(AvatarShape)
                        .background(if (popup.destructive) cs.errorContainer else cs.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (popup.destructive) Icons.Outlined.Delete else Icons.Outlined.Info,
                        contentDescription = null,
                        tint = if (popup.destructive) cs.onErrorContainer else cs.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = popup.title,
                    color = cs.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = popup.message,
                    color = cs.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (popup.destructive) {
                        FilledTonalButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = cs.onSurface.copy(alpha = 0.08f),
                                contentColor = cs.onSurface
                            )
                        ) { Text("Cancelar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
                        Button(
                            onClick = { onDismiss(); popup.onConfirm() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = cs.error, contentColor = cs.onError)
                        ) { Text(popup.confirmLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    } else {
                        PrimaryButton(
                            label = popup.confirmLabel,
                            onClick = { onDismiss(); popup.onConfirm() },
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinDialog(mode: PinMode, onDismiss: () -> Unit, onSaved: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var pin by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var mismatch by remember { mutableStateOf(false) }

    fun digit(d: String) {
        if (step == 1) {
            if (pin.length < 4) {
                pin += d
                mismatch = false
                if (pin.length == 4) step = 2
            }
        } else if (confirmation.length < 4) {
            confirmation += d
            if (confirmation.length == 4) {
                if (confirmation == pin) {
                    onSaved(pin)
                } else {
                    pin = ""
                    confirmation = ""
                    step = 1
                    mismatch = true
                }
            }
        }
    }

    fun erase() {
        if (step == 1 && pin.isNotEmpty()) pin = pin.dropLast(1)
        else if (step == 2 && confirmation.isNotEmpty()) confirmation = confirmation.dropLast(1)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogContainer(),
            border = BorderStroke(1.dp, dialogBorder()),
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().widthIn(max = 380.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconTile(Icons.Outlined.Lock, cs.primary)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when {
                        step == 2 -> "Confirmar PIN"
                        mode == PinMode.Reset -> "Redefinir PIN"
                        else -> "Criar PIN"
                    },
                    color = cs.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (step == 1) "Escolha 4 dígitos para proteger o app" else "Digite o mesmo PIN novamente",
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                PinBoxes(
                    filled = if (step == 1) pin.length else confirmation.length,
                    enabled = true,
                    box = 50.dp,
                    gap = 12.dp
                )
                Box(Modifier.height(30.dp), contentAlignment = Alignment.Center) {
                    if (mismatch) {
                        Text("Os PINs não coincidiram. Tente de novo.", color = cs.error, fontSize = 13.sp)
                    }
                }
                FlatNumpad(
                    onNumber = { digit(it) },
                    onDelete = { erase() },
                    keyHeight = 54.dp
                )
            }
        }
    }
}

@Composable
private fun SecurityScreen(
    hasPin: Boolean,
    biometricEnabled: Boolean,
    onBack: () -> Unit,
    onCreatePin: () -> Unit,
    onRemovePin: () -> Unit,
    onBiometricChange: (Boolean) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(title = "Privacidade", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Escolha como proteger seus aniversários sempre que abrir o app.",
                color = cs.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(20.dp))
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Lock, cs.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Acesso com PIN", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (hasPin) "Ativado" else "Desativado",
                            color = if (hasPin) cs.primary else cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    if (hasPin) {
                        OutlinedButton(
                            onClick = onRemovePin,
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.error),
                            border = BorderStroke(1.dp, cs.error.copy(alpha = 0.4f))
                        ) { Text("Remover", fontWeight = FontWeight.SemiBold) }
                    } else {
                        PrimaryButton(
                            label = "Configurar",
                            onClick = onCreatePin,
                            height = 40.dp,
                            fontSize = 14.sp
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                    thickness = 0.5.dp,
                    color = hairline()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Fingerprint, cs.tertiary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Impressão digital", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Desbloqueie com a biometria do aparelho",
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    Switch(checked = biometricEnabled, onCheckedChange = onBiometricChange)
                }
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ThemesScreen(
    selectedKey: String,
    appearance: AppearancePrefs,
    isDark: Boolean,
    onAppearanceChange: (AppearancePrefs) -> Unit,
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var menuOpen by remember { mutableStateOf(false) }
    val selectEnabled = !appearance.followSystem
    val entries = remember { themeEntries() }
    val activeKey = entries.firstOrNull { it.key == selectedKey }?.key ?: defaultThemeKey()

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(title = "Aparência", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            SectionTitle("Modo")
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Settings, cs.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Padrão do sistema", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Segue o tema claro ou escuro do aparelho",
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = appearance.followSystem,
                        onCheckedChange = { on ->
                            onAppearanceChange(
                                if (on) appearance.copy(followSystem = true)
                                else AppearancePrefs(followSystem = false, dark = isDark)
                            )
                        }
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                    thickness = 0.5.dp,
                    color = hairline()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (selectEnabled) 1f else 0.45f)
                        .padding(20.dp)
                ) {
                    Text("Tema", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { menuOpen = true },
                            enabled = selectEnabled,
                            shape = RoundedCornerShape(16.dp),
                            color = cs.surfaceContainerHigh,
                            border = BorderStroke(1.dp, hairline()),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.height(52.dp).padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDark) "Tema escuro" else "Tema claro",
                                    color = cs.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Outlined.ChevronRight,
                                    null,
                                    tint = cs.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp).rotate(90f)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tema claro") },
                                onClick = {
                                    menuOpen = false
                                    onAppearanceChange(AppearancePrefs(followSystem = false, dark = false))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Tema escuro") },
                                onClick = {
                                    menuOpen = false
                                    onAppearanceChange(AppearancePrefs(followSystem = false, dark = true))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            SectionTitle("Cores")
            entries.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { entry ->
                        PaletteCard(
                            entry = entry,
                            isSelected = entry.key == activeKey,
                            modifier = Modifier.weight(1f)
                        ) { onSelect(entry.key) }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.navigationBarsPadding().height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
    )
}

@Composable
private fun PaletteCard(
    entry: ThemeEntry,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val idleBorder = hairline()
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) cs.primary else idleBorder,
        animationSpec = tween(200),
        label = "paletteBorder"
    )
    val chipShape = RoundedCornerShape(12.dp)
    Surface(
        onClick = onClick,
        shape = CardShape,
        color = if (isSelected) lerp(cs.surfaceContainerLow, cs.primary, 0.08f) else cs.surfaceContainerLow,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cs.surfaceContainerHigh)
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    entry.colors.forEachIndexed { index, color ->
                        Box(
                            Modifier
                                .weight(if (index == 0) 1.6f else 1f)
                                .fillMaxHeight()
                                .clip(chipShape)
                                .background(color)
                                .border(1.dp, cs.outlineVariant.copy(alpha = 0.5f), chipShape)
                        )
                    }
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cs.primary)
                            .border(2.dp, cs.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Check, "Selecionado", tint = cs.onPrimary, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = entry.label,
                color = cs.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.description,
                color = cs.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}