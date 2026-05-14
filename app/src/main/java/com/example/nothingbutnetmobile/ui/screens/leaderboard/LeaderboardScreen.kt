package com.example.nothingbutnetmobile.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = hiltViewModel()
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sorting Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161616), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SortChip(
                    label = "Shots",
                    isSelected = uiState.sortType == LeaderboardSort.SHOTS,
                    onClick = { viewModel.setSortType(LeaderboardSort.SHOTS) }
                )
                SortChip(
                    label = "FG%",
                    isSelected = uiState.sortType == LeaderboardSort.FG_PERCENTAGE,
                    onClick = { viewModel.setSortType(LeaderboardSort.FG_PERCENTAGE) }
                )
                SortChip(
                    label = "Optimal Arc",
                    isSelected = uiState.sortType == LeaderboardSort.OPTIMAL_ARC,
                    onClick = { viewModel.setSortType(LeaderboardSort.OPTIMAL_ARC) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.rankedSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sessions to rank yet.",
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
                    itemsIndexed(uiState.rankedSessions) { index, session ->
                        LeaderboardItem(
                            rank = index + 1,
                            session = session,
                            sortType = uiState.sortType
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .height(40.dp),
        color = if (isSelected) OrangePrimary else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.Black else Color.Gray
            )
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    session: ShotAnalysis,
    sortType: LeaderboardSort
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.Transparent
    }

    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number or Medal
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (rank <= 3) rankColor else Color(0xFF262626),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (rank <= 3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Session $dateString",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "${session.makes}/${session.totalShots} makes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Highlighting the sorted stat
            Column(horizontalAlignment = Alignment.End) {
                val primaryValue = when (sortType) {
                    LeaderboardSort.SHOTS -> "${session.totalShots}"
                    LeaderboardSort.FG_PERCENTAGE -> "${session.fgPercentage.toInt()}%"
                    LeaderboardSort.OPTIMAL_ARC -> "${String.format("%.1f", session.averageAngle)}°"
                }
                
                val label = when (sortType) {
                    LeaderboardSort.SHOTS -> "Shots"
                    LeaderboardSort.FG_PERCENTAGE -> "FG%"
                    LeaderboardSort.OPTIMAL_ARC -> "Arc"
                }

                Text(
                    text = primaryValue,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
