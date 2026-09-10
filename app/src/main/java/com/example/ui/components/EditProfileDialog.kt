package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AbsentRedNeon
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazCyan

@Composable
fun EditProfileDialog(
    profile: UserProfile,
    canDelete: Boolean,
    onDismiss: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit,
    onDeleteProfile: (Long) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var selectedEmoji by remember { mutableStateOf(profile.avatarEmoji) }
    var advanceText by remember { mutableStateOf(profile.monthlyAdvancePaid.toInt().toString()) }
    var rateText by remember { mutableStateOf(profile.pricePerTiffin.toInt().toString()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val emojis = listOf("🧑‍🎓", "😎", "🧑‍💻", "👨‍🍳", "🍛", "🍱", "🚀", "🔥")

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "Profile Delete Karein? 🗑️",
                    fontWeight = FontWeight.Bold,
                    color = AbsentRedNeon
                )
            },
            text = {
                Text(
                    text = "Kya aap sach me \"${profile.name}\" ki profile aur hisaab delete karna chahte hain? Yeh wapas nahi aayega.",
                    color = DarkTextPrimary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteProfile(profile.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRedNeon)
                ) {
                    Text("Delete Karein", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Radd Karein (Cancel)", color = DarkTextSecondary)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Profile / Dost Edit Karein ✏️",
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Naam, hisaab ya rate badlein",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Emoji Selector
                Text(
                    text = "Avatar Emoji Chunein:",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojis.forEach { emoji ->
                        val isSelected = (emoji == selectedEmoji)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) FarazCyan.copy(alpha = 0.35f) else DarkCardSurfaceElevated
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 17.sp)
                        }
                    }
                }

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorText = null
                    },
                    label = { Text("Profile / Dost Ka Naam") },
                    placeholder = { Text("e.g. Faraz, Rahul") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_name_input")
                )

                // Advance & Rate Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = advanceText,
                        onValueChange = { advanceText = it },
                        label = { Text("Advance (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text("Per Tiffin (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                errorText?.let { err ->
                    Text(
                        text = err,
                        color = AbsentRedNeon,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Delete Button (if allowed)
                if (canDelete) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AbsentRedNeon),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delete Profile",
                            tint = AbsentRedNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "  Is Profile Ko Delete Karein",
                            color = AbsentRedNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Text(
                        text = "ℹ️ Yeh aakhri profile hai, isliye isko delete nahi kiya ja sakta.",
                        fontSize = 11.sp,
                        color = DarkTextSecondary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorText = "Kripya naam likhein!"
                        return@Button
                    }
                    val adv = advanceText.toDoubleOrNull() ?: profile.monthlyAdvancePaid
                    val rate = rateText.toDoubleOrNull() ?: profile.pricePerTiffin

                    onSaveProfile(
                        profile.copy(
                            name = name.trim(),
                            avatarEmoji = selectedEmoji,
                            monthlyAdvancePaid = adv,
                            pricePerTiffin = rate
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FarazCyan),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Karein", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        }
    )
}
