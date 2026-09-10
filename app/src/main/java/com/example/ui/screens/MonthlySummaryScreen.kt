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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SimpleYearMonth
import com.example.ui.MonthHistoryItem
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

@Composable
fun MonthlySummaryScreen(
    uiState: TiffinUiState,
    onSelectMonth: (SimpleYearMonth) -> Unit,
    onOpenEditHisaabDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = uiState.monthlySummary
    val activeProfile = uiState.activeProfile

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("monthly_summary_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. HEADER WITH BRANDING ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mahine Ka Hisaab 📊",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = "${activeProfile?.avatarEmoji ?: "🍱"} ${summary.profileName} • ${summary.yearMonth.displayString()}",
                        fontSize = 12.sp,
                        color = FarazCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FarazCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "✨ Made by Faraz",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarazCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // --- 2. SUMMARY CARD WITH PROMINENT AUTO-MINUS DIGIT ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_hisaab_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Aunty Ke Paas Bacha Balance:",
                        fontSize = 13.sp,
                        color = DarkTextSecondary
                    )

                    // Big glowing balance
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCardSurfaceElevated)
                            .border(1.dp, FarazCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "₹${summary.remainingBalance.toInt()}",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FarazCyan
                            )
                            Text(
                                text = "Advance ₹${summary.advancePaid.toInt()} me se katne ke baad",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    // Breakdown lines
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HisaabRow(
                            label = "Advance Diye The:",
                            value = "₹${summary.advancePaid.toInt()}",
                            color = DarkTextPrimary
                        )
                        HisaabRow(
                            label = "🟢 Tiffin Aaya (${summary.tiffinsReceived} Din × ₹${summary.pricePerTiffin.toInt()}):",
                            value = "-₹${summary.totalConsumedCost.toInt()} (minus)",
                            color = AbsentRedNeon
                        )
                        HisaabRow(
                            label = "🔴 Nhi Aaya / Chhutti (${summary.leaveDays} Din):",
                            value = "+₹${summary.moneySaved.toInt()} bache",
                            color = PresentGreenNeon
                        )
                        HisaabRow(
                            label = "⚪ Baaki Din (Unmarked):",
                            value = "${summary.unmarkedDays} Din",
                            color = DarkTextMuted
                        )

                        HorizontalDivider(
                            color = DarkCardBorder,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        HisaabRow(
                            label = "Total Bacha Hua Refund/Balance:",
                            value = "₹${summary.remainingBalance.toInt()}",
                            color = FarazCyan,
                            isBold = true
                        )
                    }

                    // Edit advance/rate button
                    OutlinedButton(
                        onClick = onOpenEditHisaabDialog,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = FarazCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Advance Paise / Tiffin Rate Badlein",
                            color = FarazCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. PAST MONTHS HISTORY ---
        item {
            Text(
                text = "📜 Purane Mahino Ka Hisaab",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(uiState.monthHistory) { history ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectMonth(history.yearMonth) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = history.yearMonth.displayString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            if (history.isCurrentMonth) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FarazCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Current",
                                        fontSize = 9.sp,
                                        color = FarazCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "🟢 ${history.tiffinsReceived} din aaya • 🔴 ${history.leaveDays} din nahi aaya (₹${history.moneySaved.toInt()} bache)",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Bacha: ₹${history.remainingBalance.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FarazCyan
                        )
                        Text(
                            text = "Advance: ₹${history.advancePaid.toInt()} • Kharch: ₹${history.totalCost.toInt()}",
                            fontSize = 10.sp,
                            color = DarkTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HisaabRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isBold) DarkTextPrimary else DarkTextSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = color,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}
