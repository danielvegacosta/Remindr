@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.content.Context
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.vega.remindr.data.BirthdayRepository
import com.vega.remindr.security.SecurityStore
import com.vega.remindr.ui.theme.RemindrTheme
import com.vega.remindr.ui.theme.deriveScheme
import com.vega.remindr.ui.theme.ThemeOption

@Composable
internal fun RemindrRoot(
    context: Context,
    window: Window,
    repository: BirthdayRepository,
    security: SecurityStore,
    applySystemBarStyle: () -> Unit,
    authenticate: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    biometricAvailable: () -> Boolean
) {
    var themeKey by remember { mutableStateOf(loadThemeKey(context)) }
    var appearance by remember { mutableStateOf(loadAppearance(context)) }
    val systemDark = isSystemInDarkTheme()
    val wantDark = resolveDarkTheme(appearance, systemDark)
    val view = LocalView.current
    val midnight = midnightThemeOption()
    val midnightKey = defaultThemeKey()
    val option = ThemeOption.entries.firstOrNull {
        it.key.trim().lowercase(PtBr) == themeKey.trim().lowercase(PtBr)
    }
    val isMidnight = themeKey.trim().lowercase(PtBr) == midnightKey.trim().lowercase(PtBr)
    val isOcean = themeKey.trim().lowercase(PtBr) == OCEAN_THEME_KEY
    val isForest = themeKey.trim().lowercase(PtBr) == FOREST_THEME_KEY
    val isAurora = themeKey.trim().lowercase(PtBr) == AURORA_THEME_KEY

    RemindrTheme(option ?: midnight ?: ThemeOption.MIDNIGHT, darkTheme = wantDark) {
        val base = MaterialTheme.colorScheme
        val baseDark = base.background.luminance() < 0.5f
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
                repository = repository,
                security = security,
                appearance = appearance,
                themeKey = themeKey,
                onAppearanceChange = { updated ->
                    appearance = updated
                    saveAppearance(context, updated)
                },
                onThemeSelected = { selected ->
                    saveThemeKey(context, selected)
                    themeKey = selected
                },
                authenticate = authenticate,
                biometricAvailable = biometricAvailable
            )
        }
    }
}
