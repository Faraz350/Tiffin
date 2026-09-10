package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.PaymentStatus
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBorder
import com.example.ui.theme.AbsentRedContainer
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBorder
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.UnmarkedGray
import com.example.ui.theme.UnmarkedGrayContainer

@Composable
fun AttendanceBadge(
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (bg, border, textCol, label) = when (status) {
        AttendanceStatus.PRESENT -> Quad(PresentGreenContainer, PresentGreenBorder, PresentGreen, "Present 🟢")
        AttendanceStatus.ABSENT -> Quad(AbsentRedContainer, AbsentRedBorder, AbsentRed, "Absent 🔴")
        AttendanceStatus.UNMARKED -> Quad(UnmarkedGrayContainer, Color.LightGray, UnmarkedGray, "Not Marked ⚪")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(textCol)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = textCol,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PaymentBadge(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val (bg, border, textCol, label) = when (status) {
        PaymentStatus.PAID -> Quad(PresentGreenContainer, PresentGreenBorder, PresentGreen, "PAID")
        PaymentStatus.PARTIAL -> Quad(Color(0xFFFFF8E1), Color(0xFFFFD54F), Color(0xFFF57F17), "PARTIAL")
        PaymentStatus.PENDING -> Quad(AbsentRedContainer, AbsentRedBorder, AbsentRed, "PENDING")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textCol,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
