package com.example.data.repository

import com.example.data.db.AttendanceDao
import com.example.data.db.CarryForwardDao
import com.example.data.db.PaymentDao
import com.example.data.db.SettingsDao
import com.example.data.db.UserProfileDao
import com.example.data.model.AppSettings
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.MonthlyCarryForward
import com.example.data.model.MonthlyPayment
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class TiffinRepository(
    private val userProfileDao: UserProfileDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val carryForwardDao: CarryForwardDao,
    private val settingsDao: SettingsDao
) {
    // User Profiles
    val allProfiles: Flow<List<UserProfile>> = userProfileDao.getAllProfiles()

    suspend fun getProfileById(id: Long): UserProfile? = userProfileDao.getProfileById(id)

    suspend fun insertProfile(profile: UserProfile): Long = userProfileDao.insertProfile(profile)

    suspend fun updateProfile(profile: UserProfile) = userProfileDao.updateProfile(profile)

    suspend fun deleteProfile(id: Long) = userProfileDao.deleteProfile(id)

    // Attendance Records
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()

    fun getAttendanceForProfile(profileId: Long): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForProfile(profileId)

    fun getAttendanceForMonth(profileId: Long, yearMonthIso: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForMonth(profileId, yearMonthIso)

    fun getAttendanceByDate(profileId: Long, dateIso: String): Flow<AttendanceRecord?> =
        attendanceDao.getAttendanceByDate(profileId, dateIso)

    suspend fun getAttendanceByDateSync(profileId: Long, dateIso: String): AttendanceRecord? =
        attendanceDao.getAttendanceByDateSync(profileId, dateIso)

    fun getAttendanceForDateAcrossProfiles(dateIso: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForDateAcrossProfiles(dateIso)

    suspend fun setAttendance(
        profileId: Long,
        dateIso: String,
        status: AttendanceStatus,
        note: String? = null
    ) {
        if (status == AttendanceStatus.UNMARKED && note.isNullOrBlank()) {
            attendanceDao.deleteByDate(profileId, dateIso)
        } else {
            attendanceDao.insertOrUpdate(
                AttendanceRecord(
                    profileId = profileId,
                    dateIso = dateIso,
                    status = status,
                    note = note?.takeIf { it.isNotBlank() },
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun insertAllAttendance(records: List<AttendanceRecord>) {
        attendanceDao.insertAll(records)
    }

    suspend fun deleteAttendance(profileId: Long, dateIso: String) {
        attendanceDao.deleteByDate(profileId, dateIso)
    }

    suspend fun clearAttendanceForMonth(profileId: Long, monthPrefix: String) {
        attendanceDao.clearAttendanceForMonth(profileId, monthPrefix)
    }

    // Monthly Payments
    fun getPaymentForMonth(yearMonthIso: String): Flow<MonthlyPayment?> =
        paymentDao.getPaymentForMonth(yearMonthIso)

    val allPayments: Flow<List<MonthlyPayment>> = paymentDao.getAllPayments()

    suspend fun savePayment(payment: MonthlyPayment) {
        paymentDao.insertOrUpdate(payment)
    }

    // Carry Forward
    fun getCarryForwardForMonth(yearMonthIso: String): Flow<MonthlyCarryForward?> =
        carryForwardDao.getCarryForwardForMonth(yearMonthIso)

    val allCarryForwards: Flow<List<MonthlyCarryForward>> = carryForwardDao.getAllCarryForwards()

    suspend fun saveCarryForward(carryForward: MonthlyCarryForward) {
        carryForwardDao.insertOrUpdate(carryForward)
    }

    // Settings
    val settings: Flow<AppSettings?> = settingsDao.getSettings()

    suspend fun updateSettings(settings: AppSettings) {
        settingsDao.insertOrUpdate(settings)
    }
}
