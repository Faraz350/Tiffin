package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    UNMARKED;

    val isPresent: Boolean get() = this == PRESENT
    val isAbsent: Boolean get() = this == ABSENT
    val isMarked: Boolean get() = this != UNMARKED

    fun label(): String = when (this) {
        PRESENT -> "Aaya (Received)"
        ABSENT -> "Nhi Aaya (Leave)"
        UNMARKED -> "Not Marked"
    }

    fun emoji(): String = when (this) {
        PRESENT -> "🟢"
        ABSENT -> "🔴"
        UNMARKED -> "⚪"
    }
}

enum class PaymentStatus {
    PAID,
    PARTIAL,
    PENDING
}

@Entity(
    tableName = "user_profiles"
)
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarEmoji: String = "🍱",
    val monthlyAdvancePaid: Double = 3000.0,
    val pricePerTiffin: Double = 50.0,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "attendance_records",
    primaryKeys = ["profileId", "dateIso"]
)
data class AttendanceRecord(
    val profileId: Long = 1L,
    val dateIso: String, // e.g. "2026-09-10"
    val status: AttendanceStatus,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "monthly_payments")
data class MonthlyPayment(
    @PrimaryKey
    val yearMonthIso: String, // e.g. "2026-09"
    val amountPaid: Double = 0.0,
    val paymentDateIso: String? = null,
    val paymentMode: String = "UPI",
    val isMarkedPaid: Boolean = false,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "monthly_carry_forwards")
data class MonthlyCarryForward(
    @PrimaryKey
    val yearMonthIso: String, // e.g. "2026-09"
    val manualCarriedSavings: Double? = null,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val pricePerTiffin: Double = 50.0,
    val currencySymbol: String = "₹",
    val providerName: String = "Tiffin Aunty",
    val billingStartDay: Int = 1,
    val activeProfileId: Long = 1L,
    val isNightReminderEnabled: Boolean = true
)
