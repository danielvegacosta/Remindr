package com.vega.remindr

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.vega.remindr.data.RemindrDatabase
import com.vega.remindr.notifications.ReminderScheduler
import com.vega.remindr.data.LocalBirthdayRepository
import com.vega.remindr.data.BirthdayRepository
import com.vega.remindr.security.SecurityStore
import com.vega.remindr.ui.RemindrRoot

class MainActivity : FragmentActivity() {
    private lateinit var database: RemindrDatabase
    private lateinit var birthdayRepository: BirthdayRepository
    private lateinit var security: SecurityStore

    private val buttonNavigation: Boolean by lazy { usesButtonNavigation() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullScreenLayout()
        applySystemBarStyle()
        database = RemindrDatabase(this)
        birthdayRepository = LocalBirthdayRepository(database)
        security = SecurityStore(database)
        setContent {
            RemindrRoot(
                context = this@MainActivity,
                window = window,
                repository = birthdayRepository,
                security = security,
                applySystemBarStyle = ::applySystemBarStyle,
                authenticate = ::authenticateWithBiometrics,
                biometricAvailable = ::biometricAvailable
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (::database.isInitialized) ReminderScheduler.scheduleAll(this)
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
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancelar")
                .build()
        )
    }
}
