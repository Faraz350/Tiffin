package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DateUtils
import com.example.data.model.SimpleDate
import com.example.ui.PastDayItem
import com.example.ui.TiffinUiState
import com.example.ui.theme.AbsentRedBorder
import com.example.ui.theme.AbsentRedDarkBg
import com.example.ui.theme.AbsentRedNeon
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazAmber
import com.example.ui.theme.FarazCyan
import com.example.ui.theme.PresentGreenBorder
import com.example.ui.theme.PresentGreenDarkBg
import com.example.ui.theme.PresentGreenNeon
import com.example.ui.theme.UnmarkedGrayDark
import com.example.ui.theme.UnmarkedGrayDarkBg

@Composable
fun TodayDashboardScreen(
    uiState: TiffinUiState,
    onMarkToday: (AttendanceStatus) -> Unit,
    onSwitchProfile: (Long) -> Unit,
    onOpenAddProfileDialog: () -> Unit,
    onOpenEditProfileDialog: (com.example.data.model.UserProfile) -> Unit,
    onOpenEditHisaabDialog: () -> Unit,
    onToggleDayStatus: (SimpleDate) -> Unit,
    onSetDayStatusDirect: (SimpleDate, AttendanceStatus) -> Unit,
    onSetFriendTodayStatus: (profileId: Long, AttendanceStatus) -> Unit,
    onSelectDayForEdit: (SimpleDate, AttendanceRecord?) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTestNotification: (Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val summary = uiState.monthlySummary
    val today = uiState.today
    val todayStatus = uiState.todayStatus
    val activeProfile = uiState.activeProfile

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("today_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. BRANDING & PROFILE SWITCHER HEADER ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Clean Date & Attendance Status Banner (No duplicate Tiffin Tracker or Made by Faraz)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📅 ${today.fullDisplayString()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = "Daily attendance aur automatic calculation",
                            fontSize = 12.sp,
                            color = DarkTextSecondary
                        )
                    }

                    // Quick Today Status Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when (todayStatus) {
                            AttendanceStatus.PRESENT -> Color(0xFF22C55E).copy(alpha = 0.2f)
                            AttendanceStatus.ABSENT -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            AttendanceStatus.UNMARKED -> Color.White.copy(alpha = 0.08f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (todayStatus) {
                                AttendanceStatus.PRESENT -> Color(0xFF22C55E)
                                AttendanceStatus.ABSENT -> Color(0xFFEF4444)
                                AttendanceStatus.UNMARKED -> DarkCardBorder
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (todayStatus) {
                                    AttendanceStatus.PRESENT -> "🟢 Aaya"
                                    AttendanceStatus.ABSENT -> "🔴 Leave"
                                    AttendanceStatus.UNMARKED -> "⏳ Not Marked"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (todayStatus) {
                                    AttendanceStatus.PRESENT -> Color(0xFF22C55E)
                                    AttendanceStatus.ABSENT -> Color(0xFFEF4444)
                                    AttendanceStatus.UNMARKED -> DarkTextSecondary
                                }
                            )
                        }
                    }
                }

                // Profile Selector Carousel Header with Edit Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profiles & Roommates:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkTextSecondary
                    )

                    activeProfile?.let { prof ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenEditProfileDialog(prof) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("btn_edit_active_profile"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = FarazCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Edit / Delete Profile ✏️",
                                color = FarazCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.profiles) { prof ->
                        val isSelected = (prof.id == activeProfile?.id)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) FarazCyan.copy(alpha = 0.2f) else DarkCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FarazCyan else DarkCardBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSwitchProfile(prof.id) }
                                .testTag("profile_chip_${prof.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = prof.avatarEmoji, fontSize = 16.sp)
                                Text(
                                    text = prof.name,
                                    color = if (isSelected) FarazCyan else DarkTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                // Edit icon directly on the chip
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit ${prof.name}",
                                    tint = if (isSelected) FarazCyan else DarkTextMuted,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onOpenEditProfileDialog(prof) }
                                )
                            }
                        }
                    }

                    // Add Profile Button
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkCardSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onOpenAddProfileDialog() }
                                .testTag("add_friend_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Friend",
                                    tint = FarazAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+ Dost Jodein",
                                    color = FarazAmber,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. NIGHT 9:00 PM REMINDER NOTIFICATION BANNER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notification",
                            tint = FarazAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "⏰ Raat 9:00 Baje Reminder",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Text(
                                text = "Har din raat 9 baje tiffin check karne ka message aayega",
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { onTestNotification(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = FarazAmber.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("test_notification_btn")
                    ) {
                        Text(
                            text = "🔔 Test Karein",
                            color = FarazAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. HERO TODAY ATTENDANCE CARD ---
        item {
            val statusColor = when (todayStatus) {
                AttendanceStatus.PRESENT -> PresentGreenNeon
                AttendanceStatus.ABSENT -> AbsentRedNeon
                AttendanceStatus.UNMARKED -> FarazCyan
            }
            val statusBg = when (todayStatus) {
                AttendanceStatus.PRESENT -> PresentGreenDarkBg
                AttendanceStatus.ABSENT -> AbsentRedDarkBg
                AttendanceStatus.UNMARKED -> DarkCardSurface
            }
            val statusBorder = when (todayStatus) {
                AttendanceStatus.PRESENT -> PresentGreenBorder
                AttendanceStatus.ABSENT -> AbsentRedBorder
                AttendanceStatus.UNMARKED -> DarkCardBorder
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = statusBg),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, statusBorder),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_today_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Tag & Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkCardSurfaceElevated,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "${activeProfile?.avatarEmoji ?: "🍱"} ${activeProfile?.name ?: "Faraz"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = today.displayString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkTextSecondary
                        )
                    }

                    // Main Big Prompt
                    Text(
                        text = when (todayStatus) {
                            AttendanceStatus.PRESENT -> "🍱 Aaj Ka Tiffin Aaya Tha! 🟢"
                            AttendanceStatus.ABSENT -> "❌ Aaj Ka Tiffin Nahi Aaya! 🔴"
                            AttendanceStatus.UNMARKED -> "🍱 Aaj Tiffin Aaya Ya Nahi Aaya?"
                        },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        textAlign = TextAlign.Center
                    )

                    // Two Big Action Buttons: "Aaya (Received)" and "Nhi Aaya (Not Received)"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AAYA (RECEIVED) BUTTON
                        Button(
                            onClick = { onMarkToday(AttendanceStatus.PRESENT) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("btn_today_aaya"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (todayStatus == AttendanceStatus.PRESENT) PresentGreenNeon else PresentGreenDarkBg
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                PresentGreenNeon
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🟢", fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = "Aaya Tha",
                                        color = if (todayStatus == AttendanceStatus.PRESENT) Color.Black else PresentGreenNeon,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Received",
                                        color = if (todayStatus == AttendanceStatus.PRESENT) Color.Black.copy(alpha = 0.8f) else PresentGreenNeon.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // NHI AAYA (NOT RECEIVED) BUTTON
                        Button(
                            onClick = { onMarkToday(AttendanceStatus.ABSENT) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("btn_today_nhi_aaya"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (todayStatus == AttendanceStatus.ABSENT) AbsentRedNeon else AbsentRedDarkBg
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                AbsentRedNeon
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🔴", fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = "Nhi Aaya",
                                        color = if (todayStatus == AttendanceStatus.ABSENT) Color.White else AbsentRedNeon,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Non-Received",
                                        color = if (todayStatus == AttendanceStatus.ABSENT) Color.White.copy(alpha = 0.8f) else AbsentRedNeon.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. PURE HISAAB CARD (AUTOMATIC MINUS IN ONE DIGIT - NO PAYMENT PORTAL) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pure_hisaab_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Month navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "💰 Tiffin Ka Hisaab",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Text(
                                text = uiState.selectedMonth.displayString(),
                                fontSize = 12.sp,
                                color = FarazCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Prev Month",
                                    tint = DarkTextSecondary
                                )
                            }
                            IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Month",
                                    tint = DarkTextSecondary
                                )
                            }
                        }
                    }

                    // Big Prominent Single-Digit Balance Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCardSurfaceElevated)
                            .border(1.dp, FarazCyan.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp, horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Bacha Hua Advance Balance (Aunty ke paas)",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${summary.remainingBalance.toInt()}",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarazCyan,
                                modifier = Modifier.testTag("remaining_balance_display")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Advance diye the: ₹${summary.advancePaid.toInt()} • Ek Tiffin: ₹${summary.pricePerTiffin.toInt()}",
                                fontSize = 11.sp,
                                color = DarkTextMuted
                            )
                        }
                    }

                    // 3 Metric Breakdown Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // AAYA PILL
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PresentGreenDarkBg)
                                .border(1.dp, PresentGreenBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "🟢 Tiffin Aaya", fontSize = 10.sp, color = PresentGreenNeon, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${summary.tiffinsReceived} Din",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkTextPrimary
                                )
                                Text(
                                    text = "-₹${summary.totalConsumedCost.toInt()} minus",
                                    fontSize = 10.sp,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        // NHI AAYA PILL
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AbsentRedDarkBg)
                                .border(1.dp, AbsentRedBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "🔴 Nhi Aaya", fontSize = 10.sp, color = AbsentRedNeon, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${summary.leaveDays} Din",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkTextPrimary
                                )
                                Text(
                                    text = "₹${summary.moneySaved.toInt()} bache",
                                    fontSize = 10.sp,
                                    color = PresentGreenNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // TOTAL DAYS PILL
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurfaceElevated)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "📅 Mahine Ke Din", fontSize = 10.sp, color = DarkTextSecondary, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${summary.totalDaysInMonth} Din",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkTextPrimary
                                )
                                Text(
                                    text = "${summary.unmarkedDays} din baaki",
                                    fontSize = 10.sp,
                                    color = DarkTextMuted
                                )
                            }
                        }
                    }

                    // Change Advance / Rate Button (No Payment Portal!)
                    OutlinedButton(
                        onClick = onOpenEditHisaabDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_change_advance_rate"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = FarazCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Advance Paise ya Tiffin Rate Badlein",
                            color = FarazCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- 5. ROOMMATES / DOST STATUS SECTION (Multi-Profile Overview) ---
        if (uiState.friendsTodayStatus.size > 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "👥 Roommates Ka Aaj Ka Tiffin",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )

                        uiState.friendsTodayStatus.forEach { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCardSurfaceElevated)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = friend.profile.avatarEmoji, fontSize = 20.sp)
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = friend.profile.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DarkTextPrimary
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit ${friend.profile.name}",
                                                tint = FarazCyan,
                                                modifier = Modifier
                                                    .size(13.dp)
                                                    .clickable { onOpenEditProfileDialog(friend.profile) }
                                            )
                                        }
                                        Text(
                                            text = when (friend.status) {
                                                AttendanceStatus.PRESENT -> "🟢 Aaya Tha"
                                                AttendanceStatus.ABSENT -> "🔴 Nhi Aaya"
                                                AttendanceStatus.UNMARKED -> "⚪ Unmarked"
                                            },
                                            fontSize = 11.sp,
                                            color = when (friend.status) {
                                                AttendanceStatus.PRESENT -> PresentGreenNeon
                                                AttendanceStatus.ABSENT -> AbsentRedNeon
                                                AttendanceStatus.UNMARKED -> DarkTextMuted
                                            }
                                        )
                                    }
                                }

                                // Quick toggle for friend
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (friend.status == AttendanceStatus.PRESENT) PresentGreenNeon else PresentGreenDarkBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PresentGreenBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onSetFriendTodayStatus(friend.profile.id, AttendanceStatus.PRESENT) }
                                    ) {
                                        Text(
                                            text = "Aaya 🟢",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (friend.status == AttendanceStatus.PRESENT) Color.Black else PresentGreenNeon,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (friend.status == AttendanceStatus.ABSENT) AbsentRedNeon else AbsentRedDarkBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AbsentRedBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onSetFriendTodayStatus(friend.profile.id, AttendanceStatus.ABSENT) }
                                    ) {
                                        Text(
                                            text = "Leave 🔴",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (friend.status == AttendanceStatus.ABSENT) Color.White else AbsentRedNeon,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. PAST DAYS QUICK EDIT (SAHI KAREIN) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("past_days_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = "📅 Purane Din Sahi Karein (Quick Edit)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = "Jo din beet gaye unhe ek tap mein 'Aaya 🟢' ya 'Nhi Aaya 🔴' set karein",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }

                    if (uiState.pastDaysOfCurrentMonth.isEmpty()) {
                        Text(
                            text = "Abhi koi purana din record nahi hai",
                            fontSize = 12.sp,
                            color = DarkTextMuted
                        )
                    } else {
                        uiState.pastDaysOfCurrentMonth.forEach { item ->
                            PastDayRowItem(
                                item = item,
                                onSetStatus = { st -> onSetDayStatusDirect(item.date, st) },
                                onClickEditNote = {
                                    val record = uiState.monthAttendance[item.date.isoString]
                                    onSelectDayForEdit(item.date, record)
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- 7. FOOTER WATERMARK / BRANDING ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✨ Crafted by Faraz • Hostel Tiffin Tracker 🍱",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarazCyan.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Seedha Hisaab • No Payment Hassles",
                        fontSize = 10.sp,
                        color = DarkTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun PastDayRowItem(
    item: PastDayItem,
    onSetStatus: (AttendanceStatus) -> Unit,
    onClickEditNote: () -> Unit
) {
    val isPresent = item.status == AttendanceStatus.PRESENT
    val isAbsent = item.status == AttendanceStatus.ABSENT

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isPresent -> PresentGreenBorder.copy(alpha = 0.4f)
                isAbsent -> AbsentRedBorder.copy(alpha = 0.4f)
                else -> DarkCardBorder
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date info & Note
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClickEditNote() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.date.displayString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    if (item.isToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FarazCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Aaj",
                                fontSize = 9.sp,
                                color = FarazCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                item.note?.let { note ->
                    Text(
                        text = "💬 $note",
                        fontSize = 11.sp,
                        color = FarazAmber,
                        maxLines = 1
                    )
                } ?: run {
                    Text(
                        text = "Tap to add note (e.g. room par nahi the)",
                        fontSize = 10.sp,
                        color = DarkTextMuted
                    )
                }
            }

            // Quick Toggle Buttons: 🟢 Aaya | 🔴 Nhi Aaya
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Aaya Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPresent) PresentGreenNeon else PresentGreenDarkBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PresentGreenBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSetStatus(AttendanceStatus.PRESENT) }
                        .testTag("day_${item.date.day}_btn_aaya")
                ) {
                    Text(
                        text = "🟢 Aaya",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPresent) Color.Black else PresentGreenNeon,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Nhi Aaya Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAbsent) AbsentRedNeon else AbsentRedDarkBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AbsentRedBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSetStatus(AttendanceStatus.ABSENT) }
                        .testTag("day_${item.date.day}_btn_nhi_aaya")
                ) {
                    Text(
                        text = "🔴 Nhi Aaya",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAbsent) Color.White else AbsentRedNeon,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
