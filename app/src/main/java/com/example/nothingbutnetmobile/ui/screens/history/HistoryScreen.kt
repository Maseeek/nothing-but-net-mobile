package com.example.nothingbutnetmobile.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nothingbutnetmobile.domain.model.ShotAnalysis
import com.example.nothingbutnetmobile.ui.components.BottomNavigationBar
import com.example.nothingbutnetmobile.ui.components.DashboardHeader
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .padding(horizontal = 20.dp)
        ) {
            DashboardHeader(userName = uiState.userName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Session History",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sessions found. Start practicing!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.sessions) { session ->
                        SessionHistoryCard(session = session)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionHistoryCard(session: ShotAnalysis) {
    val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(session.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${session.makes}/${session.totalShots}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (session.fgPercentage >= 50) Color(0xFF26A69A).copy(alpha = 0.2f) else Color(0xFFE53935).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${String.format("%.0f", session.fgPercentage)}% FG",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (session.fgPercentage >= 50) Color(0xFF26A69A) else Color(0xFFE53935)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Streak: ${session.longestStreak}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Avg Angle: ${String.format("%.1f", session.averageAngle)}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
