package com.vega.remindr.data

import com.vega.remindr.model.Birthday
import java.time.LocalDate
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {
    private val password = "senha-forte-de-teste"
    private val birthdays = listOf(
        Birthday(name = "Ana", birthDate = LocalDate.of(1992, 4, 10), notes = "Presente; café"),
        Birthday(name = "Bruno", birthDate = LocalDate.of(1988, 12, 2), notes = "")
    )

    @Test
    fun encryptedBackupRoundTripsAllFields() {
        val encoded = BackupCodec.encode(birthdays, password)

        assertTrue(encoded.startsWith("REMINDR_BACKUP_V2\n"))
        assertFalse(encoded.contains("Ana"))
        assertEquals(birthdays, BackupCodec.decode(encoded, password))
    }

    @Test
    fun wrongPasswordIsRejected() {
        val encoded = BackupCodec.encode(birthdays, password)

        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(encoded, "outra-senha")
        }
    }

    @Test
    fun encryptedBackupTamperingIsRejected() {
        val encoded = BackupCodec.encode(birthdays, password)
        val lines = encoded.trimEnd().lines()
        val payload = Base64.getDecoder().decode(lines[1])
        payload[payload.lastIndex] = (payload.last().toInt() xor 1).toByte()
        val corrupted = "${lines[0]}\n${Base64.getEncoder().encodeToString(payload)}\n"

        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(corrupted, password)
        }
    }

    @Test
    fun oldV1BackupRemainsImportableIncludingEmptyNotes() {
        fun field(value: String) = Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
        val encoded = "REMINDR_BACKUP_V1\n${field("Ana")}\t${field("1992-04-10")}\t\n"

        assertEquals(
            listOf(Birthday(name = "Ana", birthDate = LocalDate.of(1992, 4, 10), notes = "")),
            BackupCodec.decode(encoded)
        )
    }


    @Test
    fun encryptedBackupSupportsAnEmptyBirthdayList() {
        val encoded = BackupCodec.encode(emptyList(), password)

        assertEquals(emptyList<Birthday>(), BackupCodec.decode(encoded, password))
    }

    @Test
    fun malformedRecordsAreRejectedInsteadOfSilentlySkipped() {
        val encoded = "REMINDR_BACKUP_V1\nnot-a-valid-record\n"

        assertThrows(IllegalArgumentException::class.java) {
            BackupCodec.decode(encoded)
        }
    }
}
