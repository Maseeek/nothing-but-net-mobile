package com.example.nothingbutnetmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nothingbutnetmobile.data.local.TokenManager
import com.example.nothingbutnetmobile.ui.screens.auth.AuthState
import com.example.nothingbutnetmobile.ui.screens.auth.AuthViewModel
import com.example.nothingbutnetmobile.ui.screens.auth.LoginScreen
import com.example.nothingbutnetmobile.ui.screens.home.HomeScreen

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val startDestination = if (tokenManager.isLoggedIn()) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()

            // Navigate to home upon successful login
            if (authState is AuthState.Success) {
                authViewModel.resetState()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }

            LoginScreen(
                authState = authState,
                onLoginClick = { username, password ->
                    authViewModel.login(username, password)
                },
                onRegisterClick = {
                    // Navigate to register screen (to be implemented)
                }
            )
        }
        
        composable("home") {
            HomeScreen()
        }
    }
}
