package com.vega.remindr.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.vega.remindr.model.Birthday
import java.time.LocalDate

class RemindrDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE birthdays (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, birth_date TEXT NOT NULL, notes TEXT NOT NULL DEFAULT '')"
        )
        db.execSQL("CREATE TABLE config (`key` TEXT PRIMARY KEY, value TEXT NOT NULL)")
        createIndexes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var version = oldVersion
        while (version < newVersion) {
            version = when (version) {
                1 -> {
                    migrate1To2(db)
                    2
                }
                else -> throw IllegalStateException("Migração de banco não implementada: $version -> ${version + 1}")
            }
        }
    }

    private fun migrate1To2(db: SQLiteDatabase) {
        createIndexes(db)
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS index_birthdays_birth_date ON birthdays(birth_date)")
    }

    fun birthdays(): List<Birthday> = readableDatabase.rawQuery(
        "SELECT id, name, birth_date, notes FROM birthdays", null
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                runCatching {
                    Birthday(
                        id = cursor.getLong(0),
                        name = cursor.getString(1),
                        birthDate = LocalDate.parse(cursor.getString(2)),
                        notes = cursor.getString(3).orEmpty()
                    )
                }.getOrNull()?.let(::add)
            }
        }
    }

    fun birthday(id: Long): Birthday? = readableDatabase.rawQuery(
        "SELECT id, name, birth_date, notes FROM birthdays WHERE id = ?", arrayOf(id.toString())
    ).use { cursor ->
        if (!cursor.moveToFirst()) return null
        return Birthday(cursor.getLong(0), cursor.getString(1), LocalDate.parse(cursor.getString(2)), cursor.getString(3).orEmpty())
    }

    fun insert(birthday: Birthday): Long = writableDatabase.insertOrThrow(
        "birthdays", null, birthday.values()
    )

    fun insertAll(birthdays: List<Birthday>): Int {
        require(birthdays.size <= MAX_IMPORT_RECORDS) { "O backup excede o limite de registros permitido." }
        birthdays.forEach(::validateBirthday)
        if (birthdays.isEmpty()) return 0
        val db = writableDatabase
        db.beginTransaction()
        try {
            birthdays.forEach { birthday -> db.insertOrThrow("birthdays", null, birthday.values()) }
            db.setTransactionSuccessful()
            return birthdays.size
        } finally {
            db.endTransaction()
        }
    }

    fun update(birthday: Birthday): Boolean = writableDatabase.update(
        "birthdays", birthday.values(), "id = ?", arrayOf(birthday.id.toString())
    ) > 0

    fun delete(id: Long): Boolean = writableDatabase.delete(
        "birthdays", "id = ?", arrayOf(id.toString())
    ) > 0

    fun putConfig(key: String, value: String) {
        writableDatabase.insertWithOnConflict(
            "config", null, ContentValues().apply {
                put("key", key)
                put("value", value)
            }, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun putConfigs(values: Map<String, String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            values.forEach { (key, value) ->
                db.insertWithOnConflict(
                    "config", null, ContentValues().apply {
                        put("key", key)
                        put("value", value)
                    }, SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun config(key: String): String? = readableDatabase.rawQuery(
        "SELECT value FROM config WHERE `key` = ?", arrayOf(key)
    ).use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }

    fun putConfigIfChanged(key: String, value: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val current = db.rawQuery("SELECT value FROM config WHERE `key` = ?", arrayOf(key)).use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
            if (current == value) return false
            db.insertWithOnConflict(
                "config", null, ContentValues().apply {
                    put("key", key)
                    put("value", value)
                }, SQLiteDatabase.CONFLICT_REPLACE
            )
            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun removeConfig(key: String) {
        writableDatabase.delete("config", "`key` = ?", arrayOf(key))
    }

    private fun Birthday.values() = ContentValues().apply {
        put("name", name)
        put("birth_date", birthDate.toString())
        put("notes", notes)
    }

    private fun validateBirthday(birthday: Birthday) {
        require(birthday.name.isNotBlank() && birthday.name.length <= MAX_NAME_LENGTH) {
            "O nome do registro está vazio ou é longo demais."
        }
        require(birthday.notes.length <= MAX_NOTES_LENGTH) { "As anotações do registro são longas demais." }
        require(!birthday.birthDate.isAfter(LocalDate.now())) { "A data de nascimento não pode estar no futuro." }
    }

    private companion object {
        const val DATABASE_NAME = "remindr.db"
        const val DATABASE_VERSION = 2
        const val MAX_NAME_LENGTH = 200
        const val MAX_NOTES_LENGTH = 10_000
        const val MAX_IMPORT_RECORDS = 10_000
    }
}
