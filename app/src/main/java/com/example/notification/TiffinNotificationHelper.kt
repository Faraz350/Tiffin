package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object TiffinNotificationHelper {
    const val CHANNEL_ID = "tiffin_night_reminder_channel_v3"
    const val NOTIFICATION_ID = 9001
    const val ACTION_REMINDER = "com.example.ACTION_TIFFIN_REMINDER_9PM"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tiffin Night Reminder"
            val descriptionText = "Daily 9:00 PM heads-up reminder to record tiffin attendance"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleDaily9PmReminder(context: Context) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TiffinReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21) // 9:00 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If 9 PM has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: SecurityException) {
            // Inexact or exact alarm fallback
        }
    }

    fun showReminderNotification(context: Context) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            201,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIconBitmap = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.tiffin_icon_1789034971952)
        } catch (_: Exception) {
            null
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_tiffin)
            .setContentTitle("🍱 Tiffin Aaya Ya Nahi? 🌙")
            .setContentText("Raat ke 9 baj gaye! Aaj ka tiffin check karein aur tap karke status lagayein.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Raat ke 9 baj gaye Faraz bhai! 🍱\n" +
                            "Aaj ka tiffin aaya tha ya leave tha? Ek tap mein Received 🟢 ya Non-Received 🔴 mark karein taaki monthly hisaab barabar rahe! ✨"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (largeIconBitmap != null) {
            notificationBuilder.setLargeIcon(largeIconBitmap)
        }

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
