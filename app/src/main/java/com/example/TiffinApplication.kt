package com.example

import android.app.Application
import com.example.data.db.AppDatabase
import com.example.data.repository.TiffinRepository
import com.example.notification.TiffinNotificationHelper

class TiffinApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: TiffinRepository by lazy {
        TiffinRepository(
            userProfileDao = database.userProfileDao(),
            attendanceDao = database.attendanceDao(),
            paymentDao = database.paymentDao(),
            carryForwardDao = database.carryForwardDao(),
            settingsDao = database.settingsDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize daily 9:00 PM reminder
        TiffinNotificationHelper.scheduleDaily9PmReminder(this)
    }
}
