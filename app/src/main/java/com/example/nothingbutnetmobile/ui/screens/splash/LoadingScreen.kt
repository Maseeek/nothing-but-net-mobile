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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.nbn_light),
                contentDescription = "NothingButNet Icon",
                modifier = Modifier.size(76.dp)
            )
            // The text image has built-in padding, so we use a very small/negative offset or no spacer
            // to ensure it sits closely next to the icon.
            Image(
                painter = painterResource(id = R.drawable.nbn_logo_transparent),
                contentDescription = "NothingButNet Text",
                modifier = Modifier
                    .height(180.dp)
                    .offset(x = (-12).dp)
            )
        }
    }
}
