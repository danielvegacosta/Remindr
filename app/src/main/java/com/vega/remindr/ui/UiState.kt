@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.provider.Settings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

internal sealed interface Screen {
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

internal fun MainTab.label(): String = when (this) {
    MainTab.Calendar -> "Calendário"
    MainTab.Home -> "Início"
    MainTab.Settings -> "Ajustes"
}

internal fun getTabIcon(tab: MainTab, isSelected: Boolean): ImageVector = when (tab) {
    MainTab.Calendar -> if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
    MainTab.Home -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
    MainTab.Settings -> if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings
}

internal data class Popup(
    val title: String,
    val message: String,
    val confirmLabel: String = "Continuar",
    val destructive: Boolean = false,
    val onConfirm: () -> Unit
)

internal enum class PinMode { Create, Reset }

internal enum class HomeFilter(val label: String) {
    Upcoming("Próximos"),
    Month("Este mês"),
    Alphabetical("A–Z")
}
