package com.example.nothingbutnetmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nothingbutnetmobile.ui.theme.CardBackground
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary

@Composable
fun RecentSessionCard(
    percentage: Int,
    ratio: String,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(OrangePrimary, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECENT SESSION",
                    style = MaterialTheme.typography.labelMedium,
                    color = OrangePrimary,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FIELD GOAL PERCENTAGE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 72.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = ratio,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OrangePrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onViewDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "VIEW DETAILS",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                FilledIconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFF1F1D1B)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
