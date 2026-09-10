package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettings
import com.example.data.model.AttendanceRecord
import com.example.data.model.MonthlyCarryForward
import com.example.data.model.MonthlyPayment
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile): Long

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllProfiles()
}

@Dao
interface AttendanceDao {
    @Query("DELETE FROM attendance_records")
    suspend fun deleteAllAttendance()
    @Query("SELECT * FROM attendance_records WHERE profileId = :profileId ORDER BY dateIso ASC")
    fun getAttendanceForProfile(profileId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY dateIso ASC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE profileId = :profileId AND dateIso LIKE :monthPrefix || '%' ORDER BY dateIso ASC")
    fun getAttendanceForMonth(profileId: Long, monthPrefix: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE profileId = :profileId AND dateIso = :dateIso LIMIT 1")
    fun getAttendanceByDate(profileId: Long, dateIso: String): Flow<AttendanceRecord?>

    @Query("SELECT * FROM attendance_records WHERE profileId = :profileId AND dateIso = :dateIso LIMIT 1")
    suspend fun getAttendanceByDateSync(profileId: Long, dateIso: String): AttendanceRecord?

    @Query("SELECT * FROM attendance_records WHERE dateIso = :dateIso")
    fun getAttendanceForDateAcrossProfiles(dateIso: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE profileId = :profileId AND dateIso = :dateIso")
    suspend fun deleteByDate(profileId: Long, dateIso: String)

    @Query("DELETE FROM attendance_records WHERE profileId = :profileId AND dateIso LIKE :monthPrefix || '%'")
    suspend fun clearAttendanceForMonth(profileId: Long, monthPrefix: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM monthly_payments WHERE profileId = :profileId AND yearMonthIso = :yearMonthIso LIMIT 1")
    fun getPaymentForMonth(profileId: Long, yearMonthIso: String): Flow<MonthlyPayment?>

    @Query("SELECT * FROM monthly_payments WHERE profileId = :profileId AND yearMonthIso = :yearMonthIso LIMIT 1")
    suspend fun getPaymentForMonthSync(profileId: Long, yearMonthIso: String): MonthlyPayment?

    @Query("SELECT * FROM monthly_payments WHERE profileId = :profileId ORDER BY yearMonthIso DESC")
    fun getPaymentsForProfile(profileId: Long): Flow<List<MonthlyPayment>>

    @Query("SELECT * FROM monthly_payments ORDER BY yearMonthIso DESC")
    fun getAllPayments(): Flow<List<MonthlyPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(payment: MonthlyPayment)

    @Query("DELETE FROM monthly_payments WHERE profileId = :profileId AND yearMonthIso = :yearMonthIso")
    suspend fun deleteByMonth(profileId: Long, yearMonthIso: String)

    @Query("DELETE FROM monthly_payments")
    suspend fun deleteAllPayments()
}

@Dao
interface CarryForwardDao {
    @Query("SELECT * FROM monthly_carry_forwards WHERE yearMonthIso = :yearMonthIso LIMIT 1")
    fun getCarryForwardForMonth(yearMonthIso: String): Flow<MonthlyCarryForward?>

    @Query("SELECT * FROM monthly_carry_forwards ORDER BY yearMonthIso DESC")
    fun getAllCarryForwards(): Flow<List<MonthlyCarryForward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(carryForward: MonthlyCarryForward)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AppSettings)
}
