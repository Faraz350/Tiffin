package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.ui.theme.AbsentRedNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazCyan

@Composable
fun InitialSetupScreen(
    onCompleteSetup: (name: String, emoji: String, advanceAmount: Double, pricePerTiffin: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🧑‍🎓") }
    var advanceText by remember { mutableStateOf("3000") }
    var rateText by remember { mutableStateOf("50") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val emojis = listOf("🧑‍🎓", "😎", "🧑‍💻", "👨‍🍳", "🍛", "🍱", "🚀", "🔥")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("initial_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Tiffin Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(DarkCardSurfaceElevated, RoundedCornerShape(24.dp))
                    .border(2.dp, FarazCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🍱", fontSize = 42.sp)
            }

            Text(
                text = "Tiffin Tracker Mein Swagat Hai!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkTextPrimary
            )

            Text(
                text = "Sirf 30 seconds me apna setup karein. Iske baad app baaki sara hisaab khud automatic sambhalega!",
                fontSize = 13.sp,
                color = DarkTextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Setup Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Apna Naam Likhein",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FarazCyan
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                        },
                        placeholder = { Text("Aapka Naam (e.g. Faraz, Sameer)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FarazCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_name_input")
                    )

                    // Avatar Emoji Selector
                    Text(
                        text = "2. Avatar Emoji Chunein",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FarazCyan
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        emojis.forEach { emoji ->
                            val isSelected = (emoji == selectedEmoji)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) FarazCyan.copy(alpha = 0.35f) else DarkCardSurfaceElevated
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) FarazCyan else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }

                    // Advance Amount
                    Text(
                        text = "3. Aunty / Mess Ko Advance Kitna Diya? (₹)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FarazCyan
                    )

                    OutlinedTextField(
                        value = advanceText,
                        onValueChange = { advanceText = it },
                        placeholder = { Text("3000") },
                        prefix = { Text("₹ ", color = FarazCyan, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FarazCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_advance_input")
                    )

                    // Per Tiffin Rate
                    Text(
                        text = "4. Ek Tiffin Ka Rate Kitna Hai? (₹)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = FarazCyan
                    )

                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        placeholder = { Text("50") },
                        prefix = { Text("₹ ", color = FarazCyan, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FarazCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_rate_input")
                    )

                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            color = AbsentRedNeon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Highlights
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✨ Yeh App Aapke Liye Kya Karega:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = "• Roz raat 9 baje popup reminder aayega: Aaya ya Nahi?\n• Jis din khana nahi aayega, utna paisa advance se automatically bach jayega.\n• Month ke end me 100% transparent calculation ready milegi!",
                        fontSize = 12.sp,
                        color = DarkTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // Start Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Kripya apna naam darj karein!"
                        return@Button
                    }
                    val advance = advanceText.toDoubleOrNull() ?: 3000.0
                    val rate = rateText.toDoubleOrNull() ?: 50.0

                    onCompleteSetup(name.trim(), selectedEmoji, advance, rate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_start_hisaab"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FarazCyan)
            ) {
                Text(
                    text = "Hisaab Shuru Karein 🚀",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            // Made by Faraz Footer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FarazCyan.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "✨ Made by Faraz",
                    color = FarazCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
