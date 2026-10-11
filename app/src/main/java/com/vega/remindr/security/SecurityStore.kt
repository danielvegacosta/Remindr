package com.vega.remindr.security

import com.vega.remindr.data.RemindrDatabase
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class SecurityStore(private val database: RemindrDatabase) {
    val hasPin: Boolean get() = database.config(PIN_HASH) != null
    val biometricEnabled: Boolean get() = database.config(BIOMETRIC) == "true"

    fun setPin(pin: String) {
        require(pin.length == PIN_LENGTH && pin.all(Char::isDigit)) { "O PIN deve conter quatro dígitos." }
        val salt = ByteArray(SALT_BYTES).also(SecureRandom()::nextBytes)
        database.putConfigs(
            mapOf(
                PIN_SALT to Base64.getEncoder().encodeToString(salt),
                PIN_HASH to hash(pin, salt),
                PIN_FAILED_ATTEMPTS to "0",
                PIN_LOCKOUT_UNTIL to "0"
            )
        )
    }

    fun clearPin() {
        database.removeConfig(PIN_HASH)
        database.removeConfig(PIN_SALT)
        resetPinFailures()
    }

    fun verifies(pin: String): Boolean = isCorrectPin(pin, upgradeLegacyHash = true)

    fun verifyPin(pin: String): PinVerification {
        val remainingLockout = pinLockoutRemainingMillis()
        if (remainingLockout > 0L) return PinVerification.Locked(remainingLockout)

        if (isCorrectPin(pin, upgradeLegacyHash = true)) {
            resetPinFailures()
            return PinVerification.Success
        }

        val failures = (database.config(PIN_FAILED_ATTEMPTS)?.toIntOrNull() ?: 0) + 1
        if (failures >= MAX_FAILED_ATTEMPTS) {
            val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MILLIS
            database.putConfig(PIN_LOCKOUT_UNTIL, lockoutUntil.toString())
            database.putConfig(PIN_FAILED_ATTEMPTS, MAX_FAILED_ATTEMPTS.toString())
            return PinVerification.Locked(LOCKOUT_DURATION_MILLIS)
        }

        database.putConfig(PIN_FAILED_ATTEMPTS, failures.toString())
        return PinVerification.Incorrect(MAX_FAILED_ATTEMPTS - failures)
    }

    fun pinLockoutRemainingMillis(): Long {
        val until = database.config(PIN_LOCKOUT_UNTIL)?.toLongOrNull() ?: return 0L
        val remaining = until - System.currentTimeMillis()
        if (remaining <= 0L) {
            database.removeConfig(PIN_LOCKOUT_UNTIL)
            database.removeConfig(PIN_FAILED_ATTEMPTS)
            return 0L
        }
        return remaining
    }

    fun setBiometricEnabled(enabled: Boolean) = database.putConfig(BIOMETRIC, enabled.toString())
    fun theme(): String = database.config(THEME) ?: "system"
    fun setTheme(value: String) = database.putConfig(THEME, value)
    fun introSeen(): Boolean = database.config(INTRO_SEEN) == "true"
    fun setIntroSeen() = database.putConfig(INTRO_SEEN, "true")

    private fun isCorrectPin(pin: String, upgradeLegacyHash: Boolean): Boolean {
        val storedHash = database.config(PIN_HASH) ?: return false
        val encodedSalt = database.config(PIN_SALT)
        if (!encodedSalt.isNullOrBlank()) {
            val salt = runCatching { Base64.getDecoder().decode(encodedSalt) }.getOrNull() ?: return false
            val candidate = runCatching { hash(pin, salt) }.getOrNull() ?: return false
            return MessageDigest.isEqual(
                storedHash.toByteArray(Charsets.US_ASCII),
                candidate.toByteArray(Charsets.US_ASCII)
            )
        }

        val legacyCandidate = legacyHash(pin)
        val matches = MessageDigest.isEqual(
            storedHash.toByteArray(Charsets.US_ASCII),
            legacyCandidate.toByteArray(Charsets.US_ASCII)
        )
        if (matches && upgradeLegacyHash) setPin(pin)
        return matches
    }

    private fun hash(pin: String, salt: ByteArray): String = MessageDigest.getInstance("SHA-256")
        .digest(salt + pin.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }

    private fun legacyHash(pin: String): String = MessageDigest.getInstance("SHA-256")
        .digest(pin.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }

    private fun resetPinFailures() {
        database.removeConfig(PIN_FAILED_ATTEMPTS)
        database.removeConfig(PIN_LOCKOUT_UNTIL)
    }

    sealed interface PinVerification {
        data object Success : PinVerification
        data class Incorrect(val attemptsRemaining: Int) : PinVerification
        data class Locked(val remainingMillis: Long) : PinVerification
    }

    private companion object {
        const val PIN_LENGTH = 4
        const val SALT_BYTES = 16
        const val MAX_FAILED_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MILLIS = 60_000L
        const val PIN_HASH = "pin_hash"
        const val PIN_SALT = "pin_salt"
        const val PIN_FAILED_ATTEMPTS = "pin_failed_attempts"
        const val PIN_LOCKOUT_UNTIL = "pin_lockout_until"
        const val BIOMETRIC = "biometric_enabled"
        const val THEME = "theme"
        const val INTRO_SEEN = "intro_seen"
    }
}
