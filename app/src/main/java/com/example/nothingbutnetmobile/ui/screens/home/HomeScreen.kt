package com.example.nothingbutnetmobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nothingbutnetmobile.ui.components.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            DashboardHeader(userName = uiState.userName)

            Spacer(modifier = Modifier.height(24.dp))

            LiveSessionCard(
                percentage = 70,
                ratio = "7/10"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "Total Shots",
                    value = "10",
                    icon = Icons.Default.SportsBasketball,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Longest Streak",
                    value = "4",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Avg Angle",
                    value = "45.5°",
                    icon = Icons.Default.AvTimer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AIInsightsCard(
                insight = "consistent",
                tip = "Try holding your follow-through 0.2s longer."
            )

            Spacer(modifier = Modifier.height(32.dp))

            ArcAnalysisGraph()

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
