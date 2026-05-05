package com.example.nothingbutnetmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.nothingbutnetmobile.R
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigateTo = { route: String ->
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo("home") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, "Home", currentRoute == "home") {
                navigateTo("home")
            }
            NavItem(Icons.Default.Analytics, "Analysis", currentRoute == "analysis") {
                navigateTo("analysis")
            }
            
            // Space for Record FAB and its label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(64.dp)
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = "Record",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.Gray
                )
            }
            
            NavItem(Icons.Default.History, "History", currentRoute == "history") {
                navigateTo("history")
            }
            NavItem(Icons.Default.Person, "Profile", currentRoute == "profile") {
                navigateTo("profile")
            }
        }
        
        // Premium Basketball Record Button (Dome Shape)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 17.dp) // Aligns dome bottom with standard icon baseline (62dp)
                .width(80.dp)
                .height(45.dp)
                .clickable { navigateTo("record") },
            contentAlignment = Alignment.Center
        ) {
            // The Basketball Dome Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_basketball),
                contentDescription = "Record",
                tint = Color.Unspecified, // Preserve the SVG colors (orange dome, black seams)
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) OrangePrimary else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isSelected) OrangePrimary else Color.Gray
        )
    }
}
