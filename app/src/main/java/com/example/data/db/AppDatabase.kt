package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.AppSettings
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.MonthlyCarryForward
import com.example.data.model.MonthlyPayment
import com.example.data.model.UserProfile

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus?): String? = status?.name

    @TypeConverter
    fun toAttendanceStatus(value: String?): AttendanceStatus =
        value?.let { runCatching { AttendanceStatus.valueOf(it) }.getOrNull() } ?: AttendanceStatus.UNMARKED
}

@Database(
    entities = [
        UserProfile::class,
        AttendanceRecord::class,
        MonthlyPayment::class,
        MonthlyCarryForward::class,
        AppSettings::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun carryForwardDao(): CarryForwardDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tiffin_tracker_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
