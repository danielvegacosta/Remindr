package com.vega.remindr.data

import com.vega.remindr.model.Birthday

interface BirthdayRepository {
    fun birthdays(): List<Birthday>
    fun birthday(id: Long): Birthday?
    fun insert(birthday: Birthday): Long
    fun update(birthday: Birthday): Boolean
    fun delete(id: Long): Boolean
}

class LocalBirthdayRepository(
    private val database: RemindrDatabase
) : BirthdayRepository {
    override fun birthdays(): List<Birthday> = database.birthdays()

    override fun birthday(id: Long): Birthday? = database.birthday(id)

    override fun insert(birthday: Birthday): Long = database.insert(birthday)

    override fun update(birthday: Birthday): Boolean = database.update(birthday)

    override fun delete(id: Long): Boolean = database.delete(id)
}
