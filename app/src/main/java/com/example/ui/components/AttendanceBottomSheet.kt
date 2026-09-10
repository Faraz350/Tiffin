package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.SimpleDate
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBorder
import com.example.ui.theme.AbsentRedContainer
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBorder
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.UnmarkedGray
import com.example.ui.theme.UnmarkedGrayContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceBottomSheet(
    date: SimpleDate,
    currentRecord: AttendanceRecord?,
    onDismiss: () -> Unit,
    onSave: (AttendanceStatus, String?) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var selectedStatus by remember(date, currentRecord) {
        mutableStateOf(currentRecord?.status ?: AttendanceStatus.UNMARKED)
    }
    var note by remember(date, currentRecord) {
        mutableStateOf(currentRecord?.note ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = date.fullDisplayString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Update attendance & leave note",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: Present
            StatusOptionCard(
                status = AttendanceStatus.PRESENT,
                title = "Present — Tiffin Received",
                subtitle = "Mark if tiffin was delivered today (₹50)",
                isSelected = selectedStatus == AttendanceStatus.PRESENT,
                activeColor = PresentGreen,
                activeBg = PresentGreenContainer,
                activeBorder = PresentGreenBorder,
                tag = "sheet_status_present",
                onClick = { selectedStatus = AttendanceStatus.PRESENT }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Absent
            StatusOptionCard(
                status = AttendanceStatus.ABSENT,
                title = "Absent / Leave — No Tiffin",
                subtitle = "Mark holiday / leave to save money (+₹50 savings)",
                isSelected = selectedStatus == AttendanceStatus.ABSENT,
                activeColor = AbsentRed,
                activeBg = AbsentRedContainer,
                activeBorder = AbsentRedBorder,
                tag = "sheet_status_absent",
                onClick = { selectedStatus = AttendanceStatus.ABSENT }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 3: Unmarked
            StatusOptionCard(
                status = AttendanceStatus.UNMARKED,
                title = "Not Marked / Clear",
                subtitle = "Reset this day's attendance status",
                isSelected = selectedStatus == AttendanceStatus.UNMARKED,
                activeColor = UnmarkedGray,
                activeBg = UnmarkedGrayContainer,
                activeBorder = Color.LightGray,
                tag = "sheet_status_unmarked",
                onClick = { selectedStatus = AttendanceStatus.UNMARKED }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Optional note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                placeholder = { Text("e.g. Went home, Exam leave, Mess closed") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_note_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(selectedStatus, note)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_attendance_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Save Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatusOptionCard(
    status: AttendanceStatus,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    activeColor: Color,
    activeBg: Color,
    activeBorder: Color,
    tag: String,
    onClick: () -> Unit
) {
    val bg = if (isSelected) activeBg else MaterialTheme.colorScheme.surface
    val border = if (isSelected) activeBorder else MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeColor else Color.Transparent)
                .border(2.dp, if (isSelected) activeColor else Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = status.emoji(),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
