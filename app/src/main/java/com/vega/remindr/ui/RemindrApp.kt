@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.vega.remindr.data.BackupCodec
import com.vega.remindr.data.BirthdayRepository
import com.vega.remindr.notifications.ReminderScheduler
import com.vega.remindr.security.SecurityStore

@Composable
internal fun RemindrApp(
    repository: BirthdayRepository,
    security: SecurityStore,
    appearance: AppearancePrefs,
    themeKey: String,
    onAppearanceChange: (AppearancePrefs) -> Unit,
    onThemeSelected: (String) -> Unit,
    authenticate: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    biometricAvailable: () -> Boolean
) {
    val context = LocalContext.current
    var birthdays by remember { mutableStateOf(repository.birthdays().sortedBy { it.daysUntil() }) }
    var screen by remember {
        mutableStateOf<Screen>(if (!security.introSeen()) Screen.Intro else Screen.Lock)
    }
    var popup by remember { mutableStateOf<Popup?>(null) }
    var pinMode by remember { mutableStateOf<PinMode?>(null) }

    fun refresh() {
        birthdays = repository.birthdays().sortedBy { it.daysUntil() }
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
                writer.write(BackupCodec.encode(repository.birthdays()))
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
            imported.forEach { repository.insert(it) }
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
                    birthday = currentScreen.birthdayId?.let(repository::birthday),
                    onBack = { screen = if (currentScreen.birthdayId == null) Screen.Main else Screen.Details(currentScreen.birthdayId) },
                    onSave = { birthday ->
                        val saved = if (birthday.id == 0L) repository.insert(birthday).let { birthday.copy(id = it) } else {
                            repository.update(birthday)
                            birthday
                        }
                        ReminderScheduler.schedule(context, saved)
                        refresh()
                        screen = Screen.Details(saved.id)
                    }
                )
                is Screen.Details -> repository.birthday(currentScreen.birthdayId)?.let { birthday ->
                    DetailsScreen(
                        birthday = birthday,
                        onBack = { screen = Screen.Main },
                        onEdit = { screen = Screen.Editor(birthday.id) },
                        onDelete = {
                            popup = Popup("Remover registro", "Deseja remover ${birthday.name} para sempre?", "Excluir", destructive = true) {
                                repository.delete(birthday.id)
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
