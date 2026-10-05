package com.vega.remindr.security

import com.vega.remindr.data.RemindrDatabase
import java.security.MessageDigest

class SecurityStore(private val database: RemindrDatabase) {
    val hasPin: Boolean get() = database.config(PIN_HASH) != null
    val biometricEnabled: Boolean get() = database.config(BIOMETRIC) == "true"

    fun setPin(pin: String) = database.putConfig(PIN_HASH, hash(pin))
    fun clearPin() = database.removeConfig(PIN_HASH)
    fun verifies(pin: String): Boolean = database.config(PIN_HASH) == hash(pin)
    fun setBiometricEnabled(enabled: Boolean) = database.putConfig(BIOMETRIC, enabled.toString())
    fun theme(): String = database.config(THEME) ?: "system"
    fun setTheme(value: String) = database.putConfig(THEME, value)
    fun introSeen(): Boolean = database.config(INTRO_SEEN) == "true"
    fun setIntroSeen() = database.putConfig(INTRO_SEEN, "true")

    private fun hash(pin: String): String = MessageDigest.getInstance("SHA-256")
        .digest(pin.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }

    private companion object {
        const val PIN_HASH = "pin_hash"
        const val BIOMETRIC = "biometric_enabled"
        const val THEME = "theme"
        const val INTRO_SEEN = "intro_seen"
    }
}
