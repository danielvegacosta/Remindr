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
import java.time.LocalDate

class BirthdayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val birthday = RemindrDatabase(context).birthday(ReminderScheduler.idFrom(intent)) ?: return
        val today = LocalDate.now()
        val observedDay = minOf(
            birthday.birthDate.dayOfMonth,
            birthday.birthDate.month.length(today.isLeapYear)
        )
        if (birthday.birthDate.month != today.month || observedDay != today.dayOfMonth) return
        createChannel(context)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val openApp = PendingIntent.getActivity(
                context,
                birthday.id.toInt(),
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.title_white)
                .setContentTitle("Hoje e o aniversario de ${birthday.name}")
                .setContentText("Abra o Remindr para ver suas anotações.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openApp)
                .build()
                .also { context.getSystemService(NotificationManager::class.java)?.notify(birthday.id.toInt(), it) }
        }
        ReminderScheduler.schedule(context, birthday)
    }

    private fun createChannel(context: Context) {
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Lembretes de aniversario", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisos de aniversario do Remindr"
            }
        )
    }

    private companion object { const val CHANNEL_ID = "birthday_reminders" }
}
