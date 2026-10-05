package com.vega.remindr.data

import android.util.Base64
import com.vega.remindr.model.Birthday
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object BackupCodec {
    private const val HEADER = "REMINDR_BACKUP_V1"
    private const val LEGACY_KEY = "Ev3rD@t3K3y!98765432101234567890"
    private const val LEGACY_IV = "Ev3rD@t3IV!12345"

    fun encode(birthdays: List<Birthday>): String = buildString {
        appendLine(HEADER)
        birthdays.forEach { birthday ->
            append(encodeField(birthday.name))
            append('\t')
            append(encodeField(birthday.birthDate.toString()))
            append('\t')
            appendLine(encodeField(birthday.notes))
        }
    }

    fun decode(source: String): List<Birthday> {
        val clean = source.trim()
        return if (clean.startsWith(HEADER)) decodeRemindr(clean) else decodeLegacy(clean)
    }

    private fun decodeRemindr(source: String): List<Birthday> = source.lineSequence()
        .drop(1)
        .mapNotNull { line ->
            val fields = line.split('\t')
            if (fields.size != 3) return@mapNotNull null
            runCatching {
                Birthday(name = decodeField(fields[0]), birthDate = LocalDate.parse(decodeField(fields[1])), notes = decodeField(fields[2]))
            }.getOrNull()
        }
        .toList()

    private fun decodeLegacy(source: String): List<Birthday> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(LEGACY_KEY.toByteArray(StandardCharsets.UTF_8), "AES"),
            IvParameterSpec(LEGACY_IV.toByteArray(StandardCharsets.UTF_8))
        )
        val plain = String(cipher.doFinal(Base64.decode(source, Base64.DEFAULT)), StandardCharsets.UTF_8)
        return plain.lineSequence().mapNotNull { line ->
            val fields = line.split(";;")
            if (fields.size < 2) return@mapNotNull null
            runCatching {
                Birthday(name = fields[0], birthDate = LocalDate.parse(fields[1]), notes = fields.getOrElse(2) { "" })
            }.getOrNull()
        }.toList()
    }

    private fun encodeField(value: String): String = Base64.encodeToString(value.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    private fun decodeField(value: String): String = String(Base64.decode(value, Base64.NO_WRAP), StandardCharsets.UTF_8)
}
