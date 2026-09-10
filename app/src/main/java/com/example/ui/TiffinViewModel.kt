package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppSettings
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DateUtils
import com.example.data.model.MonthlyPayment
import com.example.data.model.SimpleDate
import com.example.data.model.SimpleYearMonth
import com.example.data.model.UserProfile
import com.example.data.repository.TiffinRepository
import com.example.notification.TiffinNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MonthlyHisaabSummary(
    val yearMonth: SimpleYearMonth,
    val profileName: String,
    val totalDaysInMonth: Int,
    val tiffinsReceived: Int,
    val leaveDays: Int,
    val unmarkedDays: Int,
    val pricePerTiffin: Double,
    val currencySymbol: String,
    val advancePaid: Double,
    val totalConsumedCost: Double,
    val moneySaved: Double,
    val remainingBalance: Double // Advance - Consumed (Automatic minus in one clean digit!)
)

data class PastDayItem(
    val date: SimpleDate,
    val status: AttendanceStatus,
    val note: String?,
    val isToday: Boolean,
    val isEditable: Boolean = true
)

data class FriendTodayStatus(
    val profile: UserProfile,
    val status: AttendanceStatus,
    val isCurrentActive: Boolean
)

data class MonthHistoryItem(
    val yearMonth: SimpleYearMonth,
    val tiffinsReceived: Int,
    val leaveDays: Int,
    val totalCost: Double,
    val moneySaved: Double,
    val advancePaid: Double,
    val pricePerTiffin: Double,
    val currencySymbol: String,
    val remainingBalance: Double,
    val isCurrentMonth: Boolean
)

data class TiffinUiState(
    val profiles: List<UserProfile> = emptyList(),
    val activeProfile: UserProfile? = null,
    val selectedMonth: SimpleYearMonth = DateUtils.currentYearMonth(),
    val today: SimpleDate = DateUtils.today(),
    val todayStatus: AttendanceStatus = AttendanceStatus.UNMARKED,
    val todayRecord: AttendanceRecord? = null,
    val monthAttendance: Map<String, AttendanceRecord> = emptyMap(),
    val monthlySummary: MonthlyHisaabSummary = MonthlyHisaabSummary(
        yearMonth = DateUtils.currentYearMonth(),
        profileName = "Faraz",
        totalDaysInMonth = 30,
        tiffinsReceived = 0,
        leaveDays = 0,
        unmarkedDays = 30,
        pricePerTiffin = 50.0,
        currencySymbol = "₹",
        advancePaid = 3000.0,
        totalConsumedCost = 0.0,
        moneySaved = 0.0,
        remainingBalance = 3000.0
    ),
    val pastDaysOfCurrentMonth: List<PastDayItem> = emptyList(),
    val friendsTodayStatus: List<FriendTodayStatus> = emptyList(),
    val monthHistory: List<MonthHistoryItem> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val isNightReminderActive: Boolean = true,
    val isFirstTimeLoaded: Boolean = false
)

class TiffinViewModel(
    private val repository: TiffinRepository
) : ViewModel() {

    private val _today = MutableStateFlow(DateUtils.today())
    val today: StateFlow<SimpleDate> = _today

    private val _selectedMonth = MutableStateFlow(DateUtils.currentYearMonth())
    val selectedMonth: StateFlow<SimpleYearMonth> = _selectedMonth

    private val _activeProfileId = MutableStateFlow(1L)
    val activeProfileId: StateFlow<Long> = _activeProfileId

    val profiles = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settingsFlow = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Combined UI State
    val uiState: StateFlow<TiffinUiState> = combine(
        combine(profiles, _activeProfileId, _selectedMonth) { p, id, m -> Triple(p, id, m) },
        allAttendance,
        allPayments,
        settingsFlow
    ) { (profileList, currentProfileId, selectedMonth), attendanceList, paymentsList, dbSettings ->
        val effectiveSettings = dbSettings ?: AppSettings()
        val activeProf = profileList.firstOrNull { it.id == currentProfileId }
            ?: profileList.firstOrNull { it.isDefault }
            ?: profileList.firstOrNull()

        val profId = activeProf?.id ?: 1L

        // Month-specific advance & rate (each month has its own hisaab!)
        val monthPayment = paymentsList.firstOrNull { it.profileId == profId && it.yearMonthIso == selectedMonth.isoString }
        val advancePaid = monthPayment?.amountPaid ?: activeProf?.monthlyAdvancePaid ?: 3000.0
        val pricePerTiffin = monthPayment?.pricePerTiffin ?: activeProf?.pricePerTiffin ?: effectiveSettings.pricePerTiffin
        val currency = effectiveSettings.currencySymbol

        val activeAttendance = attendanceList.filter { it.profileId == profId }
        val attendanceMap = activeAttendance.associateBy { it.dateIso }

        val today = _today.value
        val todayRecord = attendanceMap[today.isoString]
        val todayStatus = todayRecord?.status ?: AttendanceStatus.UNMARKED

        // Selected month attendance
        val daysInMonth = selectedMonth.daysInMonth()
        var tiffinsReceived = 0
        var leaveDays = 0
        var unmarkedDays = 0

        for (day in 1..daysInMonth) {
            val date = SimpleDate(selectedMonth.year, selectedMonth.month, day)
            val rec = attendanceMap[date.isoString]
            when (rec?.status) {
                AttendanceStatus.PRESENT -> tiffinsReceived++
                AttendanceStatus.ABSENT -> leaveDays++
                else -> unmarkedDays++
            }
        }

        val totalConsumedCost = tiffinsReceived * pricePerTiffin
        val moneySaved = leaveDays * pricePerTiffin
        // Hisaab auto minus in one clean digit
        val remainingBalance = advancePaid - totalConsumedCost

        val summary = MonthlyHisaabSummary(
            yearMonth = selectedMonth,
            profileName = activeProf?.name ?: "Faraz",
            totalDaysInMonth = daysInMonth,
            tiffinsReceived = tiffinsReceived,
            leaveDays = leaveDays,
            unmarkedDays = unmarkedDays,
            pricePerTiffin = pricePerTiffin,
            currencySymbol = currency,
            advancePaid = advancePaid,
            totalConsumedCost = totalConsumedCost,
            moneySaved = moneySaved,
            remainingBalance = remainingBalance
        )

        // Generate past days list for the selected month (sorted descending from today down to 1st)
        val pastDays = mutableListOf<PastDayItem>()
        val maxDayToList = if (selectedMonth == DateUtils.currentYearMonth()) {
            today.day
        } else {
            daysInMonth
        }

        for (d in maxDayToList downTo 1) {
            val date = SimpleDate(selectedMonth.year, selectedMonth.month, d)
            val rec = attendanceMap[date.isoString]
            pastDays.add(
                PastDayItem(
                    date = date,
                    status = rec?.status ?: AttendanceStatus.UNMARKED,
                    note = rec?.note,
                    isToday = (date == today)
                )
            )
        }

        // Friends / Profiles today's status across all profiles
        val friendsTodayList = profileList.map { prof ->
            val pRec = attendanceList.firstOrNull { it.profileId == prof.id && it.dateIso == today.isoString }
            FriendTodayStatus(
                profile = prof,
                status = pRec?.status ?: AttendanceStatus.UNMARKED,
                isCurrentActive = (prof.id == profId)
            )
        }

        // Monthly history items
        val currentYm = DateUtils.currentYearMonth()
        val allMonthIsos = (activeAttendance.mapNotNull { SimpleDate.parse(it.dateIso)?.yearMonthIso } +
                listOf(currentYm.isoString, currentYm.previous().isoString, selectedMonth.isoString))
            .distinct()
            .sortedDescending()

        val monthHistory = allMonthIsos.map { ymIso ->
            val ym = SimpleYearMonth.parse(ymIso) ?: SimpleYearMonth(2026, 9)
            val mDays = ym.daysInMonth()
            var presentCount = 0
            var absentCount = 0
            for (d in 1..mDays) {
                val dIso = SimpleDate(ym.year, ym.month, d).isoString
                val s = attendanceMap[dIso]?.status
                if (s == AttendanceStatus.PRESENT) presentCount++
                if (s == AttendanceStatus.ABSENT) absentCount++
            }
            val ymPayment = paymentsList.firstOrNull { it.profileId == profId && it.yearMonthIso == ym.isoString }
            val ymAdvance = ymPayment?.amountPaid ?: activeProf?.monthlyAdvancePaid ?: 3000.0
            val ymRate = ymPayment?.pricePerTiffin ?: activeProf?.pricePerTiffin ?: effectiveSettings.pricePerTiffin
            val cost = presentCount * ymRate
            val saved = absentCount * ymRate
            val bal = ymAdvance - cost
            MonthHistoryItem(
                yearMonth = ym,
                tiffinsReceived = presentCount,
                leaveDays = absentCount,
                totalCost = cost,
                moneySaved = saved,
                advancePaid = ymAdvance,
                pricePerTiffin = ymRate,
                currencySymbol = currency,
                remainingBalance = bal,
                isCurrentMonth = (ym == currentYm)
            )
        }

        TiffinUiState(
            profiles = profileList,
            activeProfile = activeProf,
            selectedMonth = selectedMonth,
            today = today,
            todayStatus = todayStatus,
            todayRecord = todayRecord,
            monthAttendance = attendanceMap,
            monthlySummary = summary,
            pastDaysOfCurrentMonth = pastDays,
            friendsTodayStatus = friendsTodayList,
            monthHistory = monthHistory,
            settings = effectiveSettings,
            isNightReminderActive = effectiveSettings.isNightReminderEnabled,
            isFirstTimeLoaded = true
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TiffinUiState()
    )

    init {
        viewModelScope.launch {
            // Check settings
            val currentSettings = repository.settings.first()
            if (currentSettings == null) {
                repository.updateSettings(
                    AppSettings(
                        pricePerTiffin = 50.0,
                        currencySymbol = "₹",
                        providerName = "Tiffin Aunty",
                        billingStartDay = 1,
                        activeProfileId = 1L,
                        isNightReminderEnabled = true
                    )
                )
            }

            // Check existing profiles
            val existingProfiles = repository.allProfiles.first()
            if (existingProfiles.isNotEmpty()) {
                _activeProfileId.value = existingProfiles.firstOrNull { it.isDefault }?.id ?: existingProfiles.first().id
            }
        }
    }

    fun createInitialProfile(
        name: String,
        emoji: String,
        advanceAmount: Double,
        pricePerTiffin: Double
    ) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                name = name,
                avatarEmoji = emoji,
                monthlyAdvancePaid = advanceAmount,
                pricePerTiffin = pricePerTiffin,
                isDefault = true
            )
            val insertedId = repository.insertProfile(newProfile)
            _activeProfileId.value = insertedId

            // Sync settings
            val cur = repository.settings.first() ?: AppSettings()
            repository.updateSettings(
                cur.copy(
                    pricePerTiffin = pricePerTiffin,
                    activeProfileId = insertedId
                )
            )

            // Save initial month payment record
            val curYm = DateUtils.currentYearMonth().isoString
            repository.savePayment(
                MonthlyPayment(
                    profileId = insertedId,
                    yearMonthIso = curYm,
                    amountPaid = advanceAmount,
                    pricePerTiffin = pricePerTiffin,
                    isMarkedPaid = true
                )
            )
        }
    }

    fun resetAllDataToFresh() {
        viewModelScope.launch {
            repository.clearAllData()
            _activeProfileId.value = 1L
        }
    }

    fun switchProfile(profileId: Long) {
        _activeProfileId.value = profileId
        viewModelScope.launch {
            val cur = repository.settings.first() ?: AppSettings()
            repository.updateSettings(cur.copy(activeProfileId = profileId))
        }
    }

    fun addProfile(name: String, emoji: String, advancePaid: Double, ratePerTiffin: Double) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                name = name.trim(),
                avatarEmoji = emoji.ifBlank { "🍱" },
                monthlyAdvancePaid = if (advancePaid > 0) advancePaid else 3000.0,
                pricePerTiffin = if (ratePerTiffin > 0) ratePerTiffin else 50.0,
                isDefault = false
            )
            val newId = repository.insertProfile(newProfile)
            _activeProfileId.value = newId
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun deleteProfile(profileId: Long) {
        viewModelScope.launch {
            val profileList = repository.allProfiles.first()
            if (profileList.size > 1) {
                repository.deleteProfile(profileId)
                val remaining = profileList.filter { it.id != profileId }
                _activeProfileId.value = remaining.first().id
            }
        }
    }

    fun markToday(status: AttendanceStatus) {
        val todayIso = _today.value.isoString
        val profId = _activeProfileId.value
        viewModelScope.launch {
            repository.setAttendance(profId, todayIso, status)
        }
    }

    fun setFriendTodayAttendance(profileId: Long, status: AttendanceStatus) {
        val todayIso = _today.value.isoString
        viewModelScope.launch {
            repository.setAttendance(profileId, todayIso, status)
        }
    }

    fun setDateAttendance(date: SimpleDate, status: AttendanceStatus, note: String? = null) {
        val profId = _activeProfileId.value
        viewModelScope.launch {
            repository.setAttendance(profId, date.isoString, status, note)
        }
    }

    fun quickToggleDayStatus(date: SimpleDate) {
        val profId = _activeProfileId.value
        val map = uiState.value.monthAttendance
        val currentStatus = map[date.isoString]?.status ?: AttendanceStatus.UNMARKED

        val nextStatus = when (currentStatus) {
            AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
            AttendanceStatus.ABSENT -> AttendanceStatus.PRESENT
            AttendanceStatus.UNMARKED -> AttendanceStatus.PRESENT
        }
        viewModelScope.launch {
            repository.setAttendance(profId, date.isoString, nextStatus)
        }
    }

    fun updateAdvanceAndRate(advancePaid: Double, ratePerTiffin: Double) {
        val profId = _activeProfileId.value
        val ym = _selectedMonth.value.isoString
        viewModelScope.launch {
            repository.savePayment(
                MonthlyPayment(
                    profileId = profId,
                    yearMonthIso = ym,
                    amountPaid = advancePaid,
                    pricePerTiffin = ratePerTiffin,
                    isMarkedPaid = true,
                    updatedAt = System.currentTimeMillis()
                )
            )
            // If the user is editing the current month, also update profile defaults so future months inherit it
            if (_selectedMonth.value == DateUtils.currentYearMonth()) {
                val active = uiState.value.activeProfile
                if (active != null) {
                    repository.updateProfile(
                        active.copy(
                            monthlyAdvancePaid = advancePaid,
                            pricePerTiffin = ratePerTiffin
                        )
                    )
                }
            }
        }
    }

    fun toggleNightReminder(context: Context, isEnabled: Boolean) {
        viewModelScope.launch {
            val cur = repository.settings.first() ?: AppSettings()
            repository.updateSettings(cur.copy(isNightReminderEnabled = isEnabled))
            if (isEnabled) {
                TiffinNotificationHelper.scheduleDaily9PmReminder(context)
            }
        }
    }

    fun testReminderNotification(context: Context) {
        TiffinNotificationHelper.showReminderNotification(context)
    }

    fun selectMonth(yearMonth: SimpleYearMonth) {
        _selectedMonth.value = yearMonth
    }

    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.previous()
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.next()
    }

    fun goToCurrentMonth() {
        _selectedMonth.value = DateUtils.currentYearMonth()
    }
}

class TiffinViewModelFactory(
    private val repository: TiffinRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TiffinViewModel::class.java)) {
            return TiffinViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
