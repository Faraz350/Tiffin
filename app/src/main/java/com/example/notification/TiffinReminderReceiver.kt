package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TiffinReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule alarm on boot
            TiffinNotificationHelper.scheduleDaily9PmReminder(context)
        } else {
            // Fire the 9 PM reminder notification
            TiffinNotificationHelper.showReminderNotification(context)
            // Ensure next day is scheduled
            TiffinNotificationHelper.scheduleDaily9PmReminder(context)
        }
    }
}
