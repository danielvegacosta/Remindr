package com.vega.remindr.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.vega.remindr.MainActivity
import com.vega.remindr.R
import com.vega.remindr.data.RemindrDatabase
import com.vega.remindr.model.Birthday
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class BirthdayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = RemindrDatabase(context.applicationContext)
        try {
            val birthday = database.birthday(ReminderScheduler.idFrom(intent)) ?: return
            ReminderScheduler.schedule(context, birthday)
            deliverIfDue(context, birthday, database)
        } finally {
            database.close()
        }
    }

    companion object {
        private const val CHANNEL_ID = "birthday_reminders"
        private const val NOTIFIED_YEAR_PREFIX = "birthday_notified_year_"
        private val REMINDER_TIME = LocalTime.of(9, 0)

        @Synchronized
        internal fun deliverIfDue(context: Context, birthday: Birthday, database: RemindrDatabase) {
            val now = ZonedDateTime.now(ZoneId.systemDefault())
            val today = now.toLocalDate()
            if (now.toLocalTime().isBefore(REMINDER_TIME)) return

            val observedDay = minOf(birthday.birthDate.dayOfMonth, birthday.birthDate.month.length(today.isLeapYear))
            if (birthday.birthDate.month != today.month || observedDay != today.dayOfMonth) return
            if (database.config(NOTIFIED_YEAR_PREFIX + birthday.id) == today.year.toString()) return
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Lembretes de aniversario", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Avisos de aniversario do Remindr"
                }
            )
            val openApp = PendingIntent.getActivity(
                context,
                birthday.id.toInt(),
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.title_white)
                .setContentTitle("Hoje e o aniversario de ${birthday.name}")
                .setContentText("Abra o Remindr para ver suas anotações.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openApp)
                .build()

            manager.notify(birthday.id.toInt(), notification)
            database.putConfigIfChanged(NOTIFIED_YEAR_PREFIX + birthday.id, today.year.toString())
        }
    }
}
