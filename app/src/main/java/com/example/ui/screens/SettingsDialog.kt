package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppSettings

@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    carriedSavings: Double,
    selectedMonthName: String,
    onDismiss: () -> Unit,
    onSaveSettings: (price: Double, currency: String, provider: String, startDay: Int) -> Unit,
    onAdjustCarriedSavings: (Double?) -> Unit
) {
    var priceText by remember { mutableStateOf(currentSettings.pricePerTiffin.toInt().toString()) }
    var currencyText by remember { mutableStateOf(currentSettings.currencySymbol) }
    var providerText by remember { mutableStateOf(currentSettings.providerName) }
    var startDayText by remember { mutableStateOf(currentSettings.billingStartDay.toString()) }
    var carrySavingsText by remember { mutableStateOf(carriedSavings.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tiffin & Rate Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tiffin price per meal
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Tiffin Price per Meal") },
                    placeholder = { Text("50") },
                    prefix = { Text(currencyText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_price_input"),
                    singleLine = true
                )

                // Currency symbol
                OutlinedTextField(
                    value = currencyText,
                    onValueChange = { currencyText = it },
                    label = { Text("Currency Symbol") },
                    placeholder = { Text("₹") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_currency_input"),
                    singleLine = true
                )

                // Provider Name
                OutlinedTextField(
                    value = providerText,
                    onValueChange = { providerText = it },
                    label = { Text("Tiffin Provider Name") },
                    placeholder = { Text("Tiffin Aunty / Mess") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_provider_input"),
                    singleLine = true
                )

                // Monthly billing starting day
                OutlinedTextField(
                    value = startDayText,
                    onValueChange = { startDayText = it },
                    label = { Text("Monthly Starting Day (1–28)") },
                    placeholder = { Text("1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_start_day_input"),
                    singleLine = true
                )

                // Carry Forward adjustment
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Carry-Forward Savings",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Carried forward to $selectedMonthName. Can be adjusted if you had prior unrecorded savings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = carrySavingsText,
                            onValueChange = { carrySavingsText = it },
                            label = { Text("Previous Savings ($currencyText)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_carry_savings_input"),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 50.0
                    val currency = currencyText.ifBlank { "₹" }
                    val provider = providerText.ifBlank { "Tiffin Aunty" }
                    val startDay = (startDayText.toIntOrNull() ?: 1).coerceIn(1, 28)
                    val newCarry = carrySavingsText.toDoubleOrNull()

                    onSaveSettings(price, currency, provider, startDay)
                    onAdjustCarriedSavings(newCarry)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
