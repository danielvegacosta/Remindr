package com.vega.remindr.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.vega.remindr.data.RemindrDatabase
import com.vega.remindr.model.Birthday
import java.time.ZoneId
import java.time.ZonedDateTime

object ReminderScheduler {
    private const val EXTRA_ID = "birthday_id"

    fun scheduleAll(context: Context) {
        val database = RemindrDatabase(context.applicationContext)
        try {
            database.birthdays().forEach { birthday ->
                schedule(context, birthday)
                runCatching { BirthdayReceiver.deliverIfDue(context, birthday, database) }
            }
        } finally {
            database.close()
        }
    }

    fun schedule(context: Context, birthday: Birthday) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val triggerAt = ReminderDateCalculator.nextTrigger(birthday.birthDate, now).toInstant().toEpochMilli()
        val pendingIntent = pendingIntent(context, birthday.id)

        val exactScheduled = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || runCatching {
            alarmManager.canScheduleExactAlarms()
        }.getOrDefault(false)
        val scheduledExactly = exactScheduled && runCatching {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }.isSuccess
        if (!scheduledExactly) {
            runCatching {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }.onFailure {
                runCatching { alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent) }
            }
        }
    }

    fun cancel(context: Context, id: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        runCatching { alarmManager.cancel(pendingIntent(context, id)) }
    }

    fun idFrom(intent: Intent): Long = intent.getLongExtra(EXTRA_ID, -1)

    private fun pendingIntent(context: Context, id: Long): PendingIntent = PendingIntent.getBroadcast(
        context,
        id.toInt(),
        Intent(context, BirthdayReceiver::class.java).putExtra(EXTRA_ID, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
