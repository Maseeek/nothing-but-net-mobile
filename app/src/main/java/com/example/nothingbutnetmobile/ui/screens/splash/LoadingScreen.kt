package com.example.nothingbutnetmobile.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nothingbutnetmobile.R
import com.example.nothingbutnetmobile.ui.theme.DarkBackground
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        // Pulse animation
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(2000) // Show for 2 seconds
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.nbn_logo_transparent),
            contentDescription = "NothingButNet Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .scale(scale.value)
        )
    }
}
