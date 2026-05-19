package com.example.nothingbutnetmobile.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nothingbutnetmobile.R
import com.example.nothingbutnetmobile.ui.theme.DarkBackground
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary
import com.example.nothingbutnetmobile.ui.theme.OrangeSecondary
import com.example.nothingbutnetmobile.ui.theme.TextWhite
import com.example.nothingbutnetmobile.ui.theme.TextGray
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.9f) }
    val progress = remember { Animatable(0f) }
    
    // Pulse animation for the logo
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Progress bar animation filling up in 2000ms
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    // Animate the ambient glow alpha/intensity
    val infiniteTransition = rememberInfiniteTransition(label = "glowTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Radial Glow (Ember)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            OrangePrimary.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        center = Offset.Unspecified,
                        radius = 1200f
                    )
                )
        )

        // Glassmorphic Container
        Box(
            modifier = Modifier
                .padding(24.dp)
                .width(300.dp)
                .background(
                    color = Color.White.copy(alpha = 0.02f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 28.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo with subtle pulse
                Image(
                    painter = painterResource(id = R.drawable.nbn_logo_transparent),
                    contentDescription = "NothingButNet Logo",
                    modifier = Modifier
                        .height(110.dp)
                        .scale(scale.value)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name
                Text(
                    text = "NOTHING BUT NET",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle / Tagline
                Text(
                    text = "SMART BASKETBALL ANALYTICS",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.5.sp,
                        color = TextGray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Ember Gradient Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(OrangePrimary, OrangeSecondary)
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

