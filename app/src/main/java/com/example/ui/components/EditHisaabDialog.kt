package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazCyan

@Composable
fun EditHisaabDialog(
    profileName: String,
    monthName: String,
    currentAdvance: Double,
    currentRate: Double,
    onDismiss: () -> Unit,
    onSave: (advancePaid: Double, ratePerTiffin: Double) -> Unit
) {
    var advanceText by remember { mutableStateOf(currentAdvance.toInt().toString()) }
    var rateText by remember { mutableStateOf(currentRate.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Tiffin Hisaab ($monthName) 💰",
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$profileName ke liye $monthName ka advance aur rate",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = advanceText,
                    onValueChange = { advanceText = it },
                    label = { Text("Aunty ko kitne advance diye the (₹)") },
                    placeholder = { Text("e.g. 3000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_advance_amount")
                )

                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("Ek din ka tiffin rate (₹)") },
                    placeholder = { Text("e.g. 50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_rate_amount")
                )

                Text(
                    text = "💡 Har din tiffin 'Aaya 🟢' ya 'Nhi Aaya 🔴' mark karne par bacha hua balance automatic minus/calculate hota rahega!",
                    style = MaterialTheme.typography.bodySmall,
                    color = FarazCyan,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val adv = advanceText.toDoubleOrNull() ?: currentAdvance
                    val rate = rateText.toDoubleOrNull() ?: currentRate
                    onSave(adv, rate)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FarazCyan),
                modifier = Modifier.testTag("save_hisaab_button")
            ) {
                Text("Save Karein", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
