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
        db.execSQL("CREATE TABLE config (key TEXT PRIMARY KEY, value TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

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

    fun insert(birthday: Birthday): Long = writableDatabase.insert(
        "birthdays", null, birthday.values()
    )

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

    fun config(key: String): String? = readableDatabase.rawQuery(
        "SELECT value FROM config WHERE key = ?", arrayOf(key)
    ).use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }

    fun removeConfig(key: String) {
        writableDatabase.delete("config", "key = ?", arrayOf(key))
    }

    private fun Birthday.values() = ContentValues().apply {
        put("name", name)
        put("birth_date", birthDate.toString())
        put("notes", notes)
    }

    private companion object {
        const val DATABASE_NAME = "remindr.db"
        const val DATABASE_VERSION = 1
    }
}
