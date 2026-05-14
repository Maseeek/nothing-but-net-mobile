package com.example.nothingbutnetmobile.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nothingbutnetmobile.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
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
            Spacer(modifier = Modifier.height(16.dp))

            // General Section
            SettingsSectionHeader("GENERAL")
            SettingsToggleItem(
                title = "Dark Theme",
                subtitle = "Enable dark mode across the app",
                icon = Icons.Default.DarkMode,
                checked = uiState.darkTheme,
                onCheckedChange = { viewModel.toggleDarkTheme(it) }
            )
            SettingsToggleItem(
                title = "Notifications",
                subtitle = "Receive training reminders and tips",
                icon = Icons.Default.Notifications,
                checked = uiState.notificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Basketball Section
            SettingsSectionHeader("BASKETBALL")
            SettingsToggleItem(
                title = "Show Shot Angles",
                subtitle = "Display arc analysis in results",
                icon = Icons.Default.Analytics,
                checked = uiState.showShotAngles,
                onCheckedChange = { viewModel.toggleShotAngles(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Account Section
            SettingsSectionHeader("ACCOUNT")
            SettingsClickItem(
                title = "Edit Profile",
                subtitle = uiState.userName,
                icon = Icons.Default.Person,
                onClick = { /* Navigate to edit profile */ }
            )
            SettingsClickItem(
                title = "Logout",
                subtitle = "Sign out of your account",
                icon = Icons.Default.Logout,
                color = Color(0xFFE53935),
                onClick = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Support Section
            SettingsSectionHeader("SUPPORT")
            SettingsClickItem(
                title = "About",
                subtitle = "Version ${uiState.appVersion}",
                icon = Icons.Default.Info,
                onClick = { /* Show about dialog */ }
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            // Branding Footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "nothingbutnet",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = OrangePrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFF161616),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = Color.LightGray)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OrangePrimary,
                    checkedTrackColor = OrangePrimary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SettingsClickItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = Color(0xFF161616),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = if (color == Color.White) Color.LightGray else color)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = color, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.DarkGray)
        }
    }
}
