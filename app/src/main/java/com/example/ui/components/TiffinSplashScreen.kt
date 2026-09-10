package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FarazAmber
import com.example.ui.theme.FarazCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TiffinSplashScreen(
    onAnimationFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.2f) }
    val contentAlpha = remember { Animatable(0f) }
    val badgeAlpha = remember { Animatable(0f) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        // Step 1: Icon spring bounce
        launch {
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.55f,
                    stiffness = 300f
                )
            )
        }
        // Step 2: Content fade in
        launch {
            delay(200)
            contentAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        }
        // Step 3: Badge fade in
        launch {
            delay(400)
            badgeAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(500)
            )
        }

        // Wait then finish
        delay(1900)
        onAnimationFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onAnimationFinish() }
            .testTag("tiffin_splash_screen")
    ) {
        // Decorative radial gradient in center
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
                .scale(pulseScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            FarazCyan.copy(alpha = 0.22f),
                            FarazAmber.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Center Content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Tiffin Icon Box
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(110.dp)
                    .background(
                        color = Color(0xFF161E2E),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer subtle border
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(2.dp, FarazCyan)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🍱",
                            fontSize = 54.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Text(
                text = "Tiffin Tracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkTextPrimary,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Hostel & Roommate Daily Attendance Hisaab",
                fontSize = 14.sp,
                color = DarkTextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Glowing Dots / Tiffin Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(contentAlpha.value)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF22C55E), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFEF4444), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(FarazCyan, CircleShape)
                )
            }
        }

        // Bottom Corner / Bottom Branding: "✨ Made by Faraz"
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(badgeAlpha.value)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FarazCyan.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FarazCyan.copy(alpha = 0.4f)),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "✨", fontSize = 14.sp)
                    Text(
                        text = "Made by Faraz",
                        color = FarazCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(text = "✨", fontSize = 14.sp)
                }
            }
        }
    }
}
