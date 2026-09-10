package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DateUtils
import com.example.data.model.SimpleDate
import com.example.data.model.SimpleYearMonth
import com.example.ui.TiffinUiState
import com.example.ui.components.AttendanceBadge
import com.example.ui.theme.AbsentRedBorder
import com.example.ui.theme.AbsentRedDarkBg
import com.example.ui.theme.AbsentRedNeon
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazCyan
import com.example.ui.theme.PresentGreenBorder
import com.example.ui.theme.PresentGreenDarkBg
import com.example.ui.theme.PresentGreenNeon

@Composable
fun CalendarScreen(
    uiState: TiffinUiState,
    onSelectDayForEdit: (SimpleDate, AttendanceRecord?) -> Unit,
    onQuickToggleStatus: (SimpleDate, AttendanceStatus) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedMonth = uiState.selectedMonth
    val today = uiState.today
    val daysInMonth = selectedMonth.daysInMonth()
    val firstDayOfWeek = DateUtils.firstDayOfWeek(selectedMonth.year, selectedMonth.month) // 1=Mon..7=Sun
    val summary = uiState.monthlySummary

    val allDatesInMonth = (1..daysInMonth).map { day ->
        val date = SimpleDate(selectedMonth.year, selectedMonth.month, day)
        val rec = uiState.monthAttendance[date.isoString]
        date to rec
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. MONTH HEADER CONTROLS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousMonth,
                            modifier = Modifier.testTag("calendar_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous",
                                tint = DarkTextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedMonth.displayString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Text(
                                text = "🟢 ${summary.tiffinsReceived} Aaya • 🔴 ${summary.leaveDays} Nhi Aaya",
                                fontSize = 12.sp,
                                color = FarazCyan
                            )
                        }

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier.testTag("calendar_next_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = DarkTextPrimary
                            )
                        }
                    }

                    if (selectedMonth != SimpleYearMonth(today.year, today.month)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(
                                onClick = onJumpToToday,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("jump_to_today_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = null,
                                    tint = FarazCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Back to Current Month", color = FarazCyan, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 2. CALENDAR GRID ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        dayLabels.forEach { label ->
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkTextSecondary,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val leadingEmptyCells = firstDayOfWeek - 1
                    val totalCells = leadingEmptyCells + daysInMonth
                    val rowsCount = (totalCells + 6) / 7

                    for (row in 0 until rowsCount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val dayNumber = cellIndex - leadingEmptyCells + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val date = SimpleDate(selectedMonth.year, selectedMonth.month, dayNumber)
                                    val record = uiState.monthAttendance[date.isoString]
                                    val isToday = date == today

                                    CalendarDayCell(
                                        date = date,
                                        record = record,
                                        isToday = isToday,
                                        onClick = { onSelectDayForEdit(date, record) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .testTag("calendar_day_$dayNumber")
                                    )
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem("🟢", "Aaya (Present)")
                        LegendItem("🔴", "Nhi Aaya (Leave)")
                        LegendItem("⚪", "Unmarked")
                    }
                }
            }
        }

        // --- 3. DAILY ATTENDANCE DETAILED LIST ---
        item {
            Text(
                text = "Daily Attendance Log (${allDatesInMonth.size} Din)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(allDatesInMonth) { (date, record) ->
            val status = record?.status ?: AttendanceStatus.UNMARKED
            val isToday = date == today

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDayForEdit(date, record) }
                    .testTag("daily_log_item_${date.day}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToday) FarazCyan.copy(alpha = 0.12f)
                    else DarkCardSurfaceElevated
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isToday) FarazCyan else DarkCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = date.displayString(),
                                fontSize = 14.sp,
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = DarkTextPrimary
                            )
                            if (isToday) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FarazCyan
                                ) {
                                    Text(
                                        text = "TODAY",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (!record?.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "💬 ${record?.note}",
                                fontSize = 11.sp,
                                color = FarazCyan
                            )
                        }
                    }

                    AttendanceBadge(status = status)
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: SimpleDate,
    record: AttendanceRecord?,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = record?.status ?: AttendanceStatus.UNMARKED

    val (bgColor, borderColor, textColor) = when (status) {
        AttendanceStatus.PRESENT -> Triple(PresentGreenDarkBg, PresentGreenBorder, PresentGreenNeon)
        AttendanceStatus.ABSENT -> Triple(AbsentRedDarkBg, AbsentRedBorder, AbsentRedNeon)
        AttendanceStatus.UNMARKED -> Triple(DarkCardSurfaceElevated, Color.Transparent, DarkTextPrimary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                width = if (isToday) 2.dp else if (status.isMarked) 1.dp else 0.dp,
                color = if (isToday) FarazCyan else borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${date.day}",
                fontSize = 12.sp,
                fontWeight = if (isToday || status.isMarked) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) FarazCyan else textColor
            )

            if (status.isMarked) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (status == AttendanceStatus.PRESENT) PresentGreenNeon else AbsentRedNeon)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = DarkTextSecondary
        )
    }
}
