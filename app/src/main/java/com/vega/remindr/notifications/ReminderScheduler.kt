package com.vega.remindr.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.vega.remindr.data.RemindrDatabase
import com.vega.remindr.model.Birthday
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduler {
    private const val EXTRA_ID = "birthday_id"

    fun scheduleAll(context: Context) {
        val database = RemindrDatabase(context)
        database.birthdays().forEach { schedule(context, it) }
    }

    fun schedule(context: Context, birthday: Birthday) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val day = minOf(birthday.birthDate.dayOfMonth, birthday.birthDate.month.length(LocalDate.now().isLeapYear))
        var next = LocalDateTime.of(LocalDate.of(LocalDate.now().year, birthday.birthDate.month, day), LocalTime.of(9, 0))
        if (!next.isAfter(LocalDateTime.now())) next = next.plusYears(1)
        val pendingIntent = pendingIntent(context, birthday.id)
        val triggerAt = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context, id: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntent(context, id))
    }

    fun idFrom(intent: Intent): Long = intent.getLongExtra(EXTRA_ID, -1)

    private fun pendingIntent(context: Context, id: Long): PendingIntent = PendingIntent.getBroadcast(
        context,
        id.toInt(),
        Intent(context, BirthdayReceiver::class.java).putExtra(EXTRA_ID, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
