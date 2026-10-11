package com.vega.remindr.data

import com.vega.remindr.model.Birthday
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.time.LocalDate
import java.util.Base64
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCodec {
    private const val HEADER_V1 = "REMINDR_BACKUP_V1"
    private const val HEADER_V2 = "REMINDR_BACKUP_V2"
    private const val LEGACY_KEY = "Ev3rD@t3K3y!98765432101234567890"
    private const val LEGACY_IV = "Ev3rD@t3IV!12345"
    private const val SALT_BYTES = 16
    private const val NONCE_BYTES = 12
    private const val TAG_BITS = 128
    private const val KEY_BITS = 256
    private const val PBKDF2_ITERATIONS = 210_000
    const val MAX_BACKUP_BYTES = 5 * 1024 * 1024
    private const val MAX_RECORDS = 10_000
    private const val MAX_NAME_LENGTH = 200
    private const val MAX_NOTES_LENGTH = 10_000
    private const val MAX_PASSWORD_LENGTH = 256

    fun requiresPassword(source: String): Boolean =
        source.removePrefix("\uFEFF").trimStart().replace("\r\n", "\n").lineSequence().firstOrNull() == HEADER_V2

    fun encode(birthdays: List<Birthday>, password: String): String {
        require(password.length in 8..MAX_PASSWORD_LENGTH) { "A senha do backup deve ter entre 8 e 256 caracteres." }
        require(birthdays.size <= MAX_RECORDS) { "O backup excede o limite de registros permitido." }
        birthdays.forEach(::validateBirthday)
        require(estimatedEncryptedSize(birthdays) <= MAX_BACKUP_BYTES) {
            "O backup excede o tamanho máximo permitido."
        }

        val plainText = serializeV1(birthdays).toByteArray(StandardCharsets.UTF_8)
        val salt = ByteArray(SALT_BYTES).also(SecureRandom()::nextBytes)
        val nonce = ByteArray(NONCE_BYTES).also(SecureRandom()::nextBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, deriveKey(password, salt), GCMParameterSpec(TAG_BITS, nonce))
        cipher.updateAAD(HEADER_V2.toByteArray(StandardCharsets.UTF_8))
        val encrypted = cipher.doFinal(plainText)
        val payload = ByteBuffer.allocate(salt.size + nonce.size + encrypted.size)
            .put(salt)
            .put(nonce)
            .put(encrypted)
            .array()
        val result = "$HEADER_V2\n${Base64.getEncoder().encodeToString(payload)}\n"
        require(result.toByteArray(StandardCharsets.UTF_8).size <= MAX_BACKUP_BYTES) {
            "O backup excede o tamanho máximo permitido."
        }
        return result
    }

    fun decode(source: String, password: String? = null): List<Birthday> {
        require(source.toByteArray(StandardCharsets.UTF_8).size <= MAX_BACKUP_BYTES) {
            "O arquivo de backup excede o tamanho máximo permitido."
        }
        val clean = source.removePrefix("\uFEFF").replace("\r\n", "\n").replace('\r', '\n').trimEnd('\n')
        require(clean.isNotBlank()) { "O arquivo de backup está vazio." }
        return when (clean.lineSequence().firstOrNull()) {
            HEADER_V2 -> decodeV2(clean, password)
            HEADER_V1 -> decodeV1(clean)
            else -> decodeLegacy(clean)
        }
    }

    private fun decodeV2(source: String, password: String?): List<Birthday> {
        require(!password.isNullOrEmpty()) { "Informe a senha usada ao exportar este backup." }
        require(password.length in 8..MAX_PASSWORD_LENGTH) { "A senha do backup deve ter entre 8 e 256 caracteres." }
        val lines = source.lines()
        require(lines.size == 2 && lines[1].isNotBlank()) { "O backup criptografado está malformado." }
        val payload = runCatching { Base64.getDecoder().decode(lines[1]) }
            .getOrElse { throw IllegalArgumentException("O backup criptografado está malformado.") }
        require(payload.size > SALT_BYTES + NONCE_BYTES + TAG_BITS / 8) {
            "O backup criptografado está incompleto."
        }
        val buffer = ByteBuffer.wrap(payload)
        val salt = ByteArray(SALT_BYTES).also(buffer::get)
        val nonce = ByteArray(NONCE_BYTES).also(buffer::get)
        val encrypted = ByteArray(buffer.remaining()).also(buffer::get)
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, deriveKey(password, salt), GCMParameterSpec(TAG_BITS, nonce))
            cipher.updateAAD(HEADER_V2.toByteArray(StandardCharsets.UTF_8))
            val plain = String(cipher.doFinal(encrypted), StandardCharsets.UTF_8)
            require(plain.lineSequence().firstOrNull() == HEADER_V1) {
                "O conteúdo do backup é inválido."
            }
            return decodeV1(plain.trimEnd('\r', '\n'))
        } catch (_: AEADBadTagException) {
            throw IllegalArgumentException("Senha incorreta ou backup danificado. Nenhum dado foi alterado.")
        } catch (error: GeneralSecurityException) {
            throw IllegalArgumentException("Não foi possível descriptografar o backup. Nenhum dado foi alterado.")
        }
    }

    private fun decodeV1(source: String): List<Birthday> {
        require(source.lineSequence().firstOrNull() == HEADER_V1) { "Cabeçalho de backup inválido." }
        return decodeRows(source) { line ->
            val fields = line.split('\t')
            require(fields.size == 3) { "Uma linha do backup tem formato inválido." }
            Birthday(
                name = decodeField(fields[0]),
                birthDate = LocalDate.parse(decodeField(fields[1])),
                notes = decodeField(fields[2])
            )
        }
    }

    private fun decodeLegacy(source: String): List<Birthday> {
        val plain = try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(LEGACY_KEY.toByteArray(StandardCharsets.UTF_8), "AES"),
                javax.crypto.spec.IvParameterSpec(LEGACY_IV.toByteArray(StandardCharsets.UTF_8))
            )
            String(cipher.doFinal(Base64.getMimeDecoder().decode(source)), StandardCharsets.UTF_8)
        } catch (_: Exception) {
            throw IllegalArgumentException("Formato de backup não reconhecido ou arquivo danificado.")
        }

        val normalizedPlain = plain.replace("\r\n", "\n").replace('\r', '\n').trimEnd('\n')
        val rows = normalizedPlain.lines()
        require(rows.isNotEmpty() && rows.any { it.isNotBlank() }) { "O backup não contém registros." }
        require(rows.none { it.isBlank() }) { "O backup contém uma linha vazia inesperada." }
        require(rows.size <= MAX_RECORDS) { "O backup excede o limite de registros permitido." }
        return rows.map { line ->
            val fields = line.split(";;", limit = 3)
            require(fields.size >= 2) { "Uma linha do backup antigo tem formato inválido." }
            Birthday(
                name = fields[0],
                birthDate = LocalDate.parse(fields[1]),
                notes = fields.getOrElse(2) { "" }
            ).also(::validateBirthday)
        }
    }

    private fun decodeRows(
        source: String,
        decodeRow: (String) -> Birthday
    ): List<Birthday> {
        val lines = source.lines().drop(1)
        if (lines.isEmpty()) return emptyList()
        require(lines.none { it.isBlank() }) { "O backup contém uma linha vazia inesperada." }
        require(lines.size <= MAX_RECORDS) { "O backup excede o limite de registros permitido." }
        return lines.map { line ->
            try {
                decodeRow(line).also(::validateBirthday)
            } catch (error: IllegalArgumentException) {
                throw IllegalArgumentException("O backup contém um registro inválido: ${error.message ?: "formato incorreto"}")
            } catch (error: Exception) {
                throw IllegalArgumentException("O backup contém um registro inválido.")
            }
        }
    }

    private fun serializeV1(birthdays: List<Birthday>): String = buildString {
        appendLine(HEADER_V1)
        birthdays.forEach { birthday ->
            append(encodeField(birthday.name))
            append('\t')
            append(encodeField(birthday.birthDate.toString()))
            append('\t')
            appendLine(encodeField(birthday.notes))
        }
    }

    private fun estimatedEncryptedSize(birthdays: List<Birthday>): Long {
        fun base64Size(byteCount: Int): Long = 4L * ((byteCount + 2L) / 3L)
        var plainSize = HEADER_V1.toByteArray(StandardCharsets.UTF_8).size.toLong() + 1L
        birthdays.forEach { birthday ->
            plainSize += base64Size(birthday.name.toByteArray(StandardCharsets.UTF_8).size) + 1L
            plainSize += base64Size(birthday.birthDate.toString().toByteArray(StandardCharsets.UTF_8).size) + 1L
            plainSize += base64Size(birthday.notes.toByteArray(StandardCharsets.UTF_8).size) + 1L
        }
        val payloadSize = SALT_BYTES.toLong() + NONCE_BYTES + plainSize + TAG_BITS / 8
        return HEADER_V2.length + 1L + base64Size(payloadSize.toInt()) + 1L
    }

    private fun validateBirthday(birthday: Birthday) {
        require(birthday.name.isNotBlank()) { "O nome de um registro não pode ficar vazio." }
        require(birthday.name.length <= MAX_NAME_LENGTH) { "O nome de um registro é longo demais." }
        require(birthday.notes.length <= MAX_NOTES_LENGTH) { "As anotações de um registro são longas demais." }
        require(!birthday.birthDate.isAfter(LocalDate.now())) { "Há uma data de nascimento no futuro." }
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val passwordChars = password.toCharArray()
        val spec = PBEKeySpec(passwordChars, salt, PBKDF2_ITERATIONS, KEY_BITS)
        passwordChars.fill('\u0000')
        return try {
            SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded, "AES")
        } finally {
            spec.clearPassword()
        }
    }

    private fun encodeField(value: String): String = Base64.getEncoder().encodeToString(value.toByteArray(StandardCharsets.UTF_8))
    private fun decodeField(value: String): String = String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8)
}
