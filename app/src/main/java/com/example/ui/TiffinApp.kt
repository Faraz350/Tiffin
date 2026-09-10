package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceRecord
import com.example.data.model.SimpleDate
import com.example.ui.components.AddProfileDialog
import com.example.ui.components.AttendanceBottomSheet
import com.example.ui.components.EditHisaabDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.MonthlySummaryScreen
import com.example.ui.screens.TodayDashboardScreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazCyan

enum class AppTab(val title: String) {
    TODAY("Today & Hisaab"),
    CALENDAR("Calendar"),
    SUMMARY("Summary & History")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiffinApp(
    viewModel: TiffinViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialogs & Sheets state
    var selectedDateForEdit by remember { mutableStateOf<Pair<SimpleDate, AttendanceRecord?>?>(null) }
    var isAddProfileDialogOpen by remember { mutableStateOf(false) }
    var isEditHisaabDialogOpen by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("tiffin_app_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tiffin Tracker 🍱",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FarazCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Faraz",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarazCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCardSurface,
                    titleContentColor = DarkTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkCardSurface,
                tonalElevation = 4.dp,
                modifier = Modifier.testTag("app_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = AppTab.TODAY.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Aaj Ka Hisaab") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FarazCyan,
                        selectedTextColor = FarazCyan,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary,
                        indicatorColor = FarazCyan.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_today")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = AppTab.CALENDAR.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Calendar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FarazCyan,
                        selectedTextColor = FarazCyan,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary,
                        indicatorColor = FarazCyan.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_calendar")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = AppTab.SUMMARY.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Hisaab Book") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FarazCyan,
                        selectedTextColor = FarazCyan,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary,
                        indicatorColor = FarazCyan.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_summary")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TodayDashboardScreen(
                    uiState = uiState,
                    onMarkToday = { status -> viewModel.markToday(status) },
                    onSwitchProfile = { id -> viewModel.switchProfile(id) },
                    onOpenAddProfileDialog = { isAddProfileDialogOpen = true },
                    onOpenEditHisaabDialog = { isEditHisaabDialogOpen = true },
                    onToggleDayStatus = { date -> viewModel.quickToggleDayStatus(date) },
                    onSetDayStatusDirect = { date, st -> viewModel.setDateAttendance(date, st) },
                    onSetFriendTodayStatus = { profId, st -> viewModel.setFriendTodayAttendance(profId, st) },
                    onSelectDayForEdit = { date, record -> selectedDateForEdit = Pair(date, record) },
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onTestNotification = { ctx -> viewModel.testReminderNotification(ctx) }
                )

                1 -> CalendarScreen(
                    uiState = uiState,
                    onSelectDayForEdit = { date, record -> selectedDateForEdit = Pair(date, record) },
                    onQuickToggleStatus = { date, status -> viewModel.setDateAttendance(date, status) },
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onJumpToToday = { viewModel.goToCurrentMonth() }
                )

                2 -> MonthlySummaryScreen(
                    uiState = uiState,
                    onSelectMonth = { ym -> viewModel.selectMonth(ym) },
                    onOpenEditHisaabDialog = { isEditHisaabDialogOpen = true }
                )
            }
        }
    }

    // Attendance Edit Sheet (Past Days & Notes)
    selectedDateForEdit?.let { (date, record) ->
        AttendanceBottomSheet(
            date = date,
            currentRecord = record,
            onDismiss = { selectedDateForEdit = null },
            onSave = { status, note ->
                viewModel.setDateAttendance(date, status, note)
            }
        )
    }

    // Add Dost / Roommate Profile Dialog
    if (isAddProfileDialogOpen) {
        AddProfileDialog(
            onDismiss = { isAddProfileDialogOpen = false },
            onAddProfile = { name, emoji, advance, rate ->
                viewModel.addProfile(name, emoji, advance, rate)
            }
        )
    }

    // Edit Advance & Rate Dialog (Pure Hisaab)
    if (isEditHisaabDialogOpen) {
        EditHisaabDialog(
            profileName = uiState.activeProfile?.name ?: "Faraz",
            currentAdvance = uiState.monthlySummary.advancePaid,
            currentRate = uiState.monthlySummary.pricePerTiffin,
            onDismiss = { isEditHisaabDialogOpen = false },
            onSave = { advance, rate ->
                viewModel.updateAdvanceAndRate(advance, rate)
            }
        )
    }
}
